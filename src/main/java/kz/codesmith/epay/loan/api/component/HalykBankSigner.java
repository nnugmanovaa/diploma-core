package kz.codesmith.epay.loan.api.component;

import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Signature;
import javax.ws.rs.ext.ParamConverter.Lazy;
import kz.codesmith.epay.loan.api.configuration.halyk.HalykBankProperties;
import lombok.SneakyThrows;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

@Component
@Lazy
public class HalykBankSigner {

  private final HalykBankProperties halykBankProperties;
  private final KeyStore keyStore = KeyStore.getInstance("JKS");
  private final Signature signature = Signature.getInstance("SHA1withRSA");
  private final ResourceLoader resourceLoader;

  @SneakyThrows
  @Autowired
  public HalykBankSigner(
      HalykBankProperties halykBankProperties,
      ResourceLoader resourceLoader
  ) throws KeyStoreException, NoSuchAlgorithmException {
    this.halykBankProperties = halykBankProperties;
    this.resourceLoader = resourceLoader;
    loadCertificateIntoKeyStore();
  }

  @SneakyThrows
  private void loadCertificateIntoKeyStore() {
    keyStore.load(resourceLoader.getResource(
        halykBankProperties.getCertificate()).getInputStream(),
        halykBankProperties.getPassword().toCharArray()
    );
  }

  @SneakyThrows
  private void initVerifier() {
    signature.initVerify(keyStore.getCertificate(halykBankProperties.getVerifyCertificateAlias()));
  }

  @SneakyThrows
  private void initSigner() {
    Key key = keyStore.getKey(
        halykBankProperties.getSignCertificateAlias(),
        halykBankProperties.getPassword().toCharArray()
    );
    signature.initSign((PrivateKey) key);
  }

  @SneakyThrows
  public byte[] sign(String plainText) {
    initSigner();
    signature.update(plainText.getBytes());
    return invert(signature.sign());
  }

  @SneakyThrows
  public boolean verify(String plainText, String signBase64Encoded) {
    initVerifier();
    signature.update(plainText.getBytes());
    return signature.verify(invert(Base64.decodeBase64(signBase64Encoded)));
  }

  private byte[] invert(byte[] sign) {
    for (int i = 0, length = sign.length; i < length / 2; ++i) {
      final byte b = sign[i];
      sign[i] = sign[length - i - 1];
      sign[length - i - 1] = b;
    }
    return sign;
  }


}
