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
import { CustomHttpResponse } from "../model/custom-http-response";

@Injectable({
  providedIn: "root",
})
export class UserService {
  private host: string = environment.apiUrl;

  constructor(private http: HttpClient) {}

  public getUsers(): Observable<User[]> {
    return this.http.get<User[]>(`${this.host}/user/list`);
  }

  public addUser(formData: FormData): Observable<User> {
    return this.http.post<User>(`${this.host}/user/add`, formData);
  }

  public updateUser(formData: FormData): Observable<User> {
    return this.http.put<User>(`${this.host}/user/update`, formData);
  }

  public resetUserPassword(email: string): Observable<CustomHttpResponse> {
    return this.http.get<CustomHttpResponse>(
      `${this.host}/user/reset-password/${email}`
    );
  }

  public deleteUser(username: string): Observable<CustomHttpResponse> {
    return this.http.delete<CustomHttpResponse>(
      `${this.host}/user/delete/${username}`
    );
  }

  public addUsersToLocalCache(users: User[]): void {
    localStorage.setItem("users", JSON.stringify(users));
  }

  public getUsersFromLocalCache(): User[] {
    if (localStorage.getItem("users")) {
      return JSON.parse(localStorage.getItem("users"));
    }
    return null;
  }

  public createUserFormData(loggedInUsername: string, user: User): FormData {
    const formData = new FormData();
    formData.append("currentUsername", loggedInUsername);
    formData.append("username", user.username);
    formData.append("firstName", user.firstName);
    formData.append("lastName", user.lastName);
    formData.append("email", user.email);
    formData.append("companyId", user.companyId);
    formData.append("role", user.role);
    formData.append("active", JSON.stringify(user.active));
    formData.append("notLocked", JSON.stringify(user.notLocked));
    return formData;
  }
}
