package kz.codesmith.epay.loan.api.service;

import java.io.InputStream;

public interface StorageService {

  void put(String key, InputStream input, String mediaType);

  InputStream get(String key);
}
