package kz.codesmith.epay.loan.api.util;

import com.creditinfo.ws.score.CigWsHeader;
import com.creditinfo.ws.score.ObjectFactory;
import java.util.function.Supplier;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.binding.soap.interceptor.AbstractSoapInterceptor;
import org.apache.cxf.headers.Header;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.jaxb.JAXBDataBinding;
import org.apache.cxf.phase.Phase;

public class PkbBehaviorAuthInterceptor extends AbstractSoapInterceptor {

  private final Supplier<CigWsHeader> authSupplier;

  public PkbBehaviorAuthInterceptor(Supplier<CigWsHeader> authSupplier) {
    super(Phase.USER_LOGICAL);
    this.authSupplier = authSupplier;
  }

  @Override
  public void handleMessage(SoapMessage message) throws Fault {
    JAXBElement<CigWsHeader> cigWsHeader = new ObjectFactory()
        .createCigWsHeader(authSupplier.get());
    try {
      Header header = new Header(
          cigWsHeader.getName(),
          cigWsHeader,
          new JAXBDataBinding(CigWsHeader.class)
      );
      message.getHeaders().add(header);
    } catch (JAXBException e) {
      throw new Fault(e);
    }
  }
}
