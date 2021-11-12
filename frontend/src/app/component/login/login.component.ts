import { HttpErrorResponse, HttpResponse } from "@angular/common/http";
import { Component, OnDestroy, OnInit } from "@angular/core";
import { Title } from "@angular/platform-browser";

import { Router } from "@angular/router";

import { HeaderType } from "src/app/enum/header-type.enum";
import { NotificationMessage } from "src/app/enum/notification-message-type.enum";
import { NotificationType } from "src/app/enum/notification-type.enum";
import { TitleType } from "src/app/enum/title-type.enum";

import { User } from "src/app/model/user";
import { AuthenticationService } from "src/app/service/authentication.service";
import { NotificationService } from "src/app/service/notification.service";
import { SubSink } from "subsink";

@Component({
  selector: "app-login",
  templateUrl: "./login.component.html",
  styleUrls: ["./login.component.css"],
})
export class LoginComponent implements OnInit, OnDestroy {
  private subs = new SubSink();

  constructor(
    private router: Router,
    private authenticationService: AuthenticationService,
    private notificationService: NotificationService,
    private title: Title
  ) {
    this.title.setTitle(TitleType.LOGIN);
  }

  ngOnInit(): void {
    if (this.authenticationService.isUserLoggedIn()) {
      this.router.navigateByUrl("/");
    } else {
      this.router.navigateByUrl("/login");
    }
  }

  public onLogin(user: User): void {
    /*TODO -> Add Validation */
    this.subs.add(
      this.authenticationService.login(user).subscribe(
        (response: HttpResponse<User>) => {
          const token = response.headers.get(HeaderType.JWT_TOKEN);
          this.authenticationService.saveToken(token);
          this.authenticationService.addUserToLocalCache(response.body);
          this.router.navigateByUrl("/");
        },
        (errorResponse: HttpErrorResponse) => {
          this.sendErrorNotification(
            NotificationType.ERROR,
            errorResponse.error.message
          );
        }
      )
    );
  }
  private sendErrorNotification(type: NotificationType, message: string): void {
    if (message) {
      this.notificationService.notify(type, message);
    } else {
      this.notificationService.notify(
        type,
        NotificationMessage.AN_ERROR_OCCURED
      );
    }
  }

  ngOnDestroy(): void {
    this.subs.unsubscribe();
  }
}
