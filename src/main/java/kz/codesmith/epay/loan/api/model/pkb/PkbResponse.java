package kz.codesmith.epay.loan.api.model.pkb;

import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import lombok.Data;

@XmlRootElement(name = "ROOT")
@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class PkbResponse {

  @XmlElement(name = "Header")
  private CustomerMainInfo customerMainInfo;

  @XmlElement(name = "Areears")
  private Check areears;

  @XmlElement(name = "Bankruptcy")
  private Check bankruptcy;

  @XmlElement(name = "FalseBusi")
  private Check falseBusiness;

  @XmlElement(name = "RNUGosZakup")
  private Check govProcurement;

  @XmlElement(name = "L150o10")
  private Check l150o10;

  @XmlElement(name = "TerrorList")
  private Check terrorList;

  @XmlElement(name = "KgdWanted")
  private Check kgbWanted;

  @XmlElement(name = "QamqorAlimony")
  private Check qamqorAlimony;

  @XmlElement(name = "QamqorList")
  private Check qamqorList;

  @XmlElement(name = "Pedophile")
  private Check pedophileList;

  @XmlElement(name = "HousingQueue")
  private Check housingQueue;

  @XmlElement(name = "EgovDebtors6M")
  private Check egovDebtors6M;

  @XmlElement(name = "EgovDebtors6MAgent")
  private Check egovDebtors6MAgent;

  @XmlElement(name = "EgovDeregistered5Year")
  private Check egovDeregistered5Year;

  @XmlElement(name = "TaxpayersDeRegistered")
  private Check taxpayersDeRegistered;

  @XmlElement(name = "TaxMonitoring")
  private Check taxMonitoring;

  @XmlElement(name = "WrongAddress")
  private Check wrongAddress;

  @XmlElement(name = "BankruptKgd")
  private Check bankruptKgd;

  @XmlElement(name = "InvalidRegistration")
  private Check invalidRegistration;

  @XmlElement(name = "LegalEntity")
  private Check legalEntity;

  @XmlElement(name = "KgdReturnedNotification")
  private Check kgdReturnedNotification;

  @XmlElement(name = "ReliableSuppliers")
  private Check reliableSuppliers;

  @XmlElement(name = "UnreliableSuppliers")
  private Check unreliableSuppliers;

  @XmlElement(name = "ViolationTaxCode")
  private Check violationTaxCode;

  @XmlElement(name = "Inactive")
  private Check inactive;

  @XmlElement(name = "EOZ")
  private Check eoz;

  @XmlElement(name = "LicenseDocument")
  private Check licenseDocument;

  @XmlElement(name = "JudicialAct")
  private Check judicialAct;

  @XmlElement(name = "TaxArrear")
  private Check taxArrear;

  @XmlElement(name = "CompanyAtTheLiquidation")
  private Check companyAtTheLiquidation;

  @XmlElement(name = "StateEnterprise")
  private Check stateEnterprise;

  @XmlElement(name = "StatDoc")
  private Check statDoc;

  @XmlElement(name = "TaxPayment")
  private Check taxPayment;

  @XmlElement(name = "DebtorBan")
  private Check debtorBan;

  @XmlElement(name = "AccrualsPayment")
  private Check accrualsPayment;

  @XmlElement(name = "RiskDegree")
  private Check riskDegree;

  @XmlElementWrapper(name = "Dynamics")
  @XmlElement(name = "Dynamic")
  List<DynamicCheck> dynamicChecks;


}
