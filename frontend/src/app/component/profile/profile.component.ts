import { HttpErrorResponse } from "@angular/common/http";
import { Component, OnInit } from "@angular/core";
import { NgForm } from "@angular/forms";
import { NotificationMessage } from "src/app/enum/notification-message-type.enum";
import { NotificationType } from "src/app/enum/notification-type.enum";
import { CustomHttpResponse } from "src/app/model/custom-http-response";
import { User } from "src/app/model/user";
import { AuthenticationService } from "src/app/service/authentication.service";
import { NotificationService } from "src/app/service/notification.service";
import { UserService } from "src/app/service/user.service";
import { SubSink } from "subsink";

@Component({
  selector: "app-profile",
  templateUrl: "./profile.component.html",
  styleUrls: ["./profile.component.css"],
})
export class ProfileComponent implements OnInit {
  public currentUser: User;
  public isAdmin: boolean;
  private subs = new SubSink();
  constructor(
    private authenticationService: AuthenticationService,
    private notificationService: NotificationService,
    private userService: UserService
  ) {}

  ngOnInit(): void {
    this.currentUser = this.authenticationService.getUserFromLocalCache();
    this.isAdmin = this.authenticationService.isAdmin;
  }

  onUpdateCurrentUser(updateUserForm: NgForm): void {
    const currentUsername = this.authenticationService.getUserFromLocalCache()
      .username;
    if (confirm(`Are you sure you want to update ${currentUsername}`)) {
      const formData = this.userService.createUserFormData(
        currentUsername,
        updateUserForm.value
      );
      this.subs.add(
        this.userService.updateUser(formData).subscribe(
          (response: User) => {
            this.authenticationService.addUserToLocalCache(response);
            this.currentUser = this.authenticationService.getUserFromLocalCache();
            this.sendNotification(
              NotificationType.SUCCESS,
              `${response.username} updated succesfully`
            );
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
  }

  public onResetPassword(email: string): void {
    if (confirm(`Are you sure you want to reset password on ${email}`)) {
      this.subs.add(
        this.userService.resetUserPassword(email).subscribe(
          (response: CustomHttpResponse) => {
            this.sendNotification(NotificationType.SUCCESS, response.message);
          },
          (errorResponse: HttpErrorResponse) => {
            this.sendNotification(
              NotificationType.WARNING,
              errorResponse.error.message
            );
          }
        )
      );
    }
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
