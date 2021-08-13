package kz.codesmith.epay.loan.api.service;

import kz.codesmith.epay.loan.api.model.documents.LoanDebtorFormPdfDto;

public interface IDocumentCreatePdf {

  byte[] createLoanDebtorFormPdf(LoanDebtorFormPdfDto dto);
}
