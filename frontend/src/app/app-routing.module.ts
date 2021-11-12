import { NgModule } from "@angular/core";
import { Title } from "@angular/platform-browser";
import { Routes, RouterModule } from "@angular/router";
import { ArticleComponent } from "./component/article/article.component";
import { ExamComponent } from "./component/exam/exam.component";
import { HomeComponent } from "./component/home/home.component";
import { LoginComponent } from "./component/login/login.component";
import { ProfileComponent } from "./component/profile/profile.component";
import { RatingComponent } from "./component/rating/rating.component";
import { RegisterComponent } from "./component/register/register.component";
import { ScheduleComponent } from "./component/schedule/schedule.component";
import { SettingsComponent } from "./component/settings/settings.component";
import { UserComponent } from "./component/user/user.component";
import { TitleType } from "./enum/title-type.enum";
import { AuthenticationGuard } from "./guard/authentication.guard";

const childRoutes: Routes = [
  {
    path: "settings",
    component: SettingsComponent,
    data: { title: TitleType.SETTINGS },
  },
  {
    path: "profile",
    component: ProfileComponent,
    data: { title: TitleType.PROFILE },
  },
  {
    path: "users",
    component: UserComponent,
    data: { title: TitleType.USERS },
  },
  { path: "exam", component: ExamComponent, data: { title: TitleType.EXAM } },
  {
    path: "rating",
    component: RatingComponent,
    data: { title: TitleType.RATING },
  },
  {
    path: "schedule",
    component: ScheduleComponent,
    data: { title: TitleType.SCHEDULE },
  },
  { path: "", component: ArticleComponent, data: { title: TitleType.ARTICLE } },
];

const routes: Routes = [
  {
    path: "login",
    component: LoginComponent,
    data: { title: TitleType.LOGIN },
  },
  {
    path: "register",
    component: RegisterComponent,
    data: { title: TitleType.REGISTER },
  },
  {
    path: "",
    component: HomeComponent,
    canActivate: [AuthenticationGuard],
    children: childRoutes,
  },
  { path: "**", redirectTo: "/login", pathMatch: "full" },
];

@NgModule({
  // imports: [RouterModule.forRoot(routes)],
  imports: [RouterModule.forRoot(routes, { useHash: false })],
  exports: [RouterModule],
})
export class AppRoutingModule {}
