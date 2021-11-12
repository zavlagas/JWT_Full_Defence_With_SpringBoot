import { Component, OnInit } from "@angular/core";
import { Title } from "@angular/platform-browser";
import { ActivatedRoute, NavigationEnd, Router } from "@angular/router";
import { BehaviorSubject } from "rxjs";
import { TitleType } from "src/app/enum/title-type.enum";
import { User } from "src/app/model/user";
import { AuthenticationService } from "src/app/service/authentication.service";
import { filter, map } from "rxjs/operators";
import { NotificationType } from "src/app/enum/notification-type.enum";
import { NotificationMessage } from "src/app/enum/notification-message-type.enum";
import { NotificationService } from "src/app/service/notification.service";
@Component({
  selector: "app-home",
  templateUrl: "./home.component.html",
  styleUrls: ["./home.component.css"],
})
export class HomeComponent implements OnInit {
  public activeUser: User;
  public isSidebarHidden = false;
  public isAdmin: boolean;
  private titleSubject = new BehaviorSubject<string>(TitleType.ARTICLE);
  public titleAction$ = this.titleSubject.asObservable();

  constructor(
    private title: Title,
    private authenticationService: AuthenticationService,
    private router: Router,
    private activatedRoute: ActivatedRoute,
    private notificationService: NotificationService
  ) {
    this.router.events
      .pipe(
        filter((event) => event instanceof NavigationEnd),
        map(() => {
          let child = this.activatedRoute.firstChild;
          while (child) {
            if (child.firstChild) {
              child = child.firstChild;
            } else if (child.snapshot.data && child.snapshot.data["title"]) {
              return child.snapshot.data["title"];
            } else {
              return null;
            }
          }
          return null;
        })
      )
      .subscribe((title: string) => {
        if (title) {
          this.changePageTitle(title);
        }
      });
  }

  ngOnInit(): void {
    this.activeUser = this.authenticationService.getUserFromLocalCache();
    this.isAdmin = this.authenticationService.isAdmin;
  }
  public changePageTitle(title: string): void {
    this.titleSubject.next(title);
    this.title.setTitle(title);
  }

  toggleSidebar() {
    this.isSidebarHidden = !this.isSidebarHidden;
  }

  public onLogout(): void {
    if (confirm(`Are you sure you want to logout ?`)) {
      this.authenticationService.logOut();
      this.router.navigate(["/login"]);
      this.sendNotification(
        NotificationType.SUCCESS,
        NotificationMessage.LOGGED_OUT_SUCCESSFULLY
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
}
