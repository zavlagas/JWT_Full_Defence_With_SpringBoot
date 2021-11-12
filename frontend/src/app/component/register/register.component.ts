import { HttpErrorResponse } from "@angular/common/http";
import { Component, OnDestroy, OnInit } from "@angular/core";
import { NgForm } from "@angular/forms";
import { Title } from "@angular/platform-browser";
import { Router } from "@angular/router";
import { Subscription } from "rxjs";
import { NotificationMessage } from "src/app/enum/notification-message-type.enum";
import { NotificationType } from "src/app/enum/notification-type.enum";
import { TitleType } from "src/app/enum/title-type.enum";
import { User } from "src/app/model/user";
import { AuthenticationService } from "src/app/service/authentication.service";
import { NotificationService } from "src/app/service/notification.service";
import { SubSink } from "subsink";

@Component({
  selector: "app-register",
  templateUrl: "./register.component.html",
  styleUrls: ["./register.component.css"],
})
export class RegisterComponent implements OnInit, OnDestroy {
  private subs = new SubSink();
  constructor(
    private router: Router,
    private authenticationService: AuthenticationService,
    private notificationService: NotificationService,
    private title: Title
  ) {
    this.title.setTitle(TitleType.REGISTER);
  }

  ngOnInit(): void {
    if (this.authenticationService.isUserLoggedIn()) {
      this.router.navigateByUrl("/");
    }
  }

  public onRegister(form: NgForm): void {
    /*TODO -> Add Validation */
    this.subs.add(
      this.authenticationService.register(form.value).subscribe(
        (response: User) => {
          this.sendNotification(
            NotificationType.SUCCESS,
            `A new account was created for ${response.firstName}. 
            Please check your email for the password to log in.`
          );

          form.resetForm();
        },
        (errorResponse: HttpErrorResponse) => {
          this.sendNotification(
            NotificationType.ERROR,
            errorResponse.error.message
          );
        }
      )
    );
  }
  private sendNotification(type: NotificationType, message: string): void {
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
