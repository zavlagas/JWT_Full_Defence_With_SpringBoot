import { HttpErrorResponse } from "@angular/common/http";
import { Component, OnDestroy, OnInit } from "@angular/core";
import { NgForm } from "@angular/forms";
import { NotificationMessage } from "src/app/enum/notification-message-type.enum";
import { NotificationType } from "src/app/enum/notification-type.enum";
import { CustomHttpResponse } from "src/app/model/custom-http-response";
import { User } from "src/app/model/user";
import { AuthenticationService } from "src/app/service/authentication.service";
import { NotificationService } from "src/app/service/notification.service";
import { UserService } from "src/app/service/user.service";
import { SubSink } from "subsink";

/*TODO -> Change User variables to soldier */
@Component({
  selector: "app-user",
  templateUrl: "./user.component.html",
  styleUrls: ["./user.component.css"],
})
export class UserComponent implements OnInit, OnDestroy {
  private subs = new SubSink();
  public users: User[];
  public refreshing: boolean;

  public selectedUser: User;
  public isAdmin: boolean;
  public editUser = new User();
  private currentUsername: string;

  constructor(
    private authenticationService: AuthenticationService,
    private userService: UserService,
    private notificationService: NotificationService
  ) {}

  ngOnInit(): void {
    this.getUsers(true);
    this.isAdmin = this.authenticationService.isAdmin;
  }

  getUsers(showNotification: boolean): void {
    this.refreshing = true;
    this.subs.add(
      this.userService.getUsers().subscribe(
        (response: User[]) => {
          this.userService.addUsersToLocalCache(response);
          this.users = response;
          this.refreshing = false;
          if (showNotification) {
            this.sendNotification(
              NotificationType.SUCCESS,
              `${response.length} user(s) loaded succesfully`
            );
          }
        },
        (errorResponse: HttpErrorResponse) => {
          this.sendNotification(
            NotificationType.ERROR,
            errorResponse.error.message
          );
          this.refreshing = false;
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

  public searchUsers(searchTerm: string): void {
    const searchTermToLowerCase = searchTerm.toLowerCase();
    const results = this.userService.getUsersFromLocalCache().filter((user) => {
      return (
        user.username.toLowerCase().indexOf(searchTermToLowerCase) !== -1 ||
        user.firstName.toLowerCase().indexOf(searchTermToLowerCase) !== -1 ||
        user.lastName.toLowerCase().indexOf(searchTermToLowerCase) !== -1 ||
        user.username.toLowerCase().indexOf(searchTermToLowerCase) !== -1 ||
        user.email.toLowerCase().indexOf(searchTermToLowerCase) !== -1 ||
        user.userId.toLowerCase().indexOf(searchTermToLowerCase) !== -1 ||
        user.companyId.toLowerCase().indexOf(searchTermToLowerCase) !== -1
      );
    });
    this.users = results;
    if (results.length === 0 || !searchTerm) {
      this.users = this.userService.getUsersFromLocalCache();
    }
  }

  public onSelectUser(selectedUser: User): void {
    this.selectedUser = selectedUser;
    this.createAndClickButton("openUserRead", "#readUserModal");
  }

  public onAddNewUserButton(): void {
    this.createAndClickButton("openUserCreate", "#createUserModal");
  }

  public addNewUser(userForm: NgForm): void {
    const formData = this.userService.createUserFormData(null, userForm.value);
    this.subs.add(
      this.userService.addUser(formData).subscribe(
        (response: User) => {
          this.findButtonAndClickIt("closeAddNewUserModal");

          this.getUsers(false);
          userForm.reset();
          this.sendNotification(
            NotificationType.SUCCESS,
            `${response.username} added succesfully`
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

  private createAndClickButton(id: string, dataBsTarget: string): void {
    const button = document.createElement("button");
    document.body.appendChild(button);
    button.setAttribute("hidden", "true");
    button.setAttribute("type", "button");
    button.setAttribute("class", "btn btn-primary");
    button.setAttribute("class", "btn btn-primary");
    button.setAttribute("id", id);
    button.setAttribute("data-bs-toggle", "modal");
    button.setAttribute("data-bs-target", dataBsTarget);
    button.click();
  }

  public onEditUser(editUser: User): void {
    this.editUser = editUser;
    this.currentUsername = editUser.username;
    this.createAndClickButton("openUserEdit", "#editUserModal");
  }

  public onUpdateUser(): void {
    const formData = this.userService.createUserFormData(
      this.currentUsername,
      this.editUser
    );
    this.subs.add(
      this.userService.updateUser(formData).subscribe(
        (response: User) => {
          this.findButtonAndClickIt("closeEditUserModal");
          this.getUsers(false);
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

  public onDeleteUser(username: string): void {
    if (confirm(`Are you sure you want delete user with id :  ${username}`)) {
      this.subs.add(
        this.userService.deleteUser(username).subscribe(
          (response: CustomHttpResponse) => {
            this.sendNotification(NotificationType.SUCCESS, response.message);
            this.getUsers(true);
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

  private findButtonAndClickIt(buttonId: string): void {
    document.getElementById(buttonId).click();
  }

  public onResetPassword(email: string): void {
    if (confirm(`Are you sure you want to reset password on ${email}`)) {
      this.refreshing = true;
      this.subs.add(
        this.userService.resetUserPassword(email).subscribe(
          (response: CustomHttpResponse) => {
            this.sendNotification(NotificationType.SUCCESS, response.message);
            this.refreshing = false;
          },
          (errorResponse: HttpErrorResponse) => {
            this.sendNotification(
              NotificationType.WARNING,
              errorResponse.error.message
            );
            this.refreshing = false;
          }
        )
      );
    }
  }
  ngOnDestroy(): void {
    this.subs.unsubscribe();
  }
}
