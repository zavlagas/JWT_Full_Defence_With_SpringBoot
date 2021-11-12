import { NgModule } from "@angular/core";
import { BrowserModule } from "@angular/platform-browser";
import { HttpClientModule, HTTP_INTERCEPTORS } from "@angular/common/http";
import { AppComponent } from "./app.component";
import { AppRoutingModule } from "./app-routing.module";
import { AuthenticationService } from "./service/authentication.service";
import { UserService } from "./service/user.service";
import { AuthInterceptor } from "./interceptor/auth.interceptor";
import { AuthenticationGuard } from "./guard/authentication.guard";
import { NotificationModule } from "./notification.module";
import { NotificationService } from "./service/notification.service";
import { FormsModule } from "@angular/forms";
import { LoginComponent } from "./component/login/login.component";
import { RegisterComponent } from "./component/register/register.component";
import { HomeComponent } from "./component/home/home.component";
import { ArticleComponent } from "./component/article/article.component";
import { ExamComponent } from "./component/exam/exam.component";
import { ScheduleComponent } from "./component/schedule/schedule.component";
import { RatingComponent } from "./component/rating/rating.component";
import { ProfileComponent } from "./component/profile/profile.component";
import { SettingsComponent } from "./component/settings/settings.component";
import { UserComponent } from "./component/user/user.component";

@NgModule({
  declarations: [
    AppComponent,
    LoginComponent,
    RegisterComponent,
    HomeComponent,
    ArticleComponent,
    ExamComponent,
    ScheduleComponent,
    RatingComponent,
    ProfileComponent,
    SettingsComponent,
    UserComponent,
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
    NotificationModule,
    FormsModule,
  ],
  providers: [
    AuthenticationGuard,
    AuthenticationService,
    NotificationService,
    UserService,
    { provide: HTTP_INTERCEPTORS, useClass: AuthInterceptor, multi: true },
  ],
  bootstrap: [AppComponent],
})
export class AppModule {
  constructor() {}
}
