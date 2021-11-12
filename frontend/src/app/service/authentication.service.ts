import { Injectable } from "@angular/core";
import {
  HttpClient,
  HttpResponse,
  HttpErrorResponse,
} from "@angular/common/http";
import { environment } from "src/environments/environment";
import { Observable } from "rxjs";
import { User } from "../model/user";
import { JwtHelperService } from "@auth0/angular-jwt";
import { Role } from "../enum/role.enum";

@Injectable({
  providedIn: "root",
})
export class AuthenticationService {
  private host: string = environment.apiUrl;
  private token: string;
  private loggedInUsername: string;
  private jwtHelper = new JwtHelperService();

  constructor(private http: HttpClient) {}

  public login(user: User): Observable<HttpResponse<User>> {
    return this.http.post<User>(`${this.host}/user/login`, user, {
      observe: "response",
    });
  }

  public register(user: User): Observable<User> {
    return this.http.post<User>(`${this.host}/user/register`, user);
  }

  public logOut(): void {
    this.token = null;
    this.loggedInUsername = null;
    localStorage.removeItem("user");
    localStorage.removeItem("token");
    localStorage.removeItem("users");
  }

  public getHost(): string {
    return this.host;
  }

  /* TODO -> Generate a random String and append it 
            before and after the jsonToken */
  /* TODO-> Change to inMemoryLocalCache */
  /* TODO -> Change The name of the localStorage Token key String */

  public saveToken(token: string): void {
    this.token = token;
    localStorage.setItem("token", token);
  }

  /* TODO > Extract Token and serialize it */
  public loadToken(): void {
    this.token = localStorage.getItem("token");
  }

  public getToken(): string {
    return this.token;
  }

  public addUserToLocalCache(user: User): void {
    localStorage.setItem("user", JSON.stringify(user));
  }

  public getUserFromLocalCache(): User {
    return JSON.parse(localStorage.getItem("user"));
  }

  public get isAdmin(): boolean {
    return this.getUserRole() === Role.ROLE_ADMIN;
  }

  private getUserRole(): string {
    return this.getUserFromLocalCache().role;
  }

  /* TODO -> Check @angular-jwt library vulnerabilities 
            https://www.npmjs.com/package/@auth0/angular-jwt */
  public isUserLoggedIn(): boolean {
    this.loadToken();
    if (this.token !== null && this.token !== "") {
      if (this.jwtHelper.decodeToken(this.token).sub != null || "") {
        if (!this.jwtHelper.isTokenExpired(this.token)) {
          this.loggedInUsername = this.jwtHelper.decodeToken(this.token).sub;
          return true;
        }
        return false;
      }
      return false;
    } else {
      this.logOut();
      return false;
    }
  }
}
