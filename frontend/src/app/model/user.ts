export class User {
  public userId: string;
  public firstName: string;
  public lastName: string;
  public email: string;
  public username: string;
  public companyId: string;
  public lastLoginDateDisplay: Date;
  public lastLoginDate: Date;
  public signUpDate: Date;
  public role: string;
  public active: boolean;
  public notLocked: boolean;

  constructor() {
    this.userId = "";
    this.firstName = "";
    this.lastName = "";
    this.email = "";
    this.username = "";
    this.companyId = "";
    this.notLocked = false;
    this.active = false;
    this.role = "";
  }
}
