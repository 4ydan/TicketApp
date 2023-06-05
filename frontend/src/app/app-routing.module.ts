import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {LoginComponent} from './components/login/login.component';
import {MessageComponent} from './components/message/message.component';
import {MessageDetailsComponent} from './components/message/details/message-details.component';
import {
  MessageCreateEditComponent,
  MessageCreateEditMode
} from './components/message/message-create-edit/message-create-edit.component';
import {HallPlanComponent} from './components/hall-plan/hall-plan.component';
import {HallPlanAppLayoutComponent} from './components/hall-plan/hall-plan-app-layout/hall-plan-app-layout.component';
import {PerformanceComponent} from './components/performance/performance.component';
import {PerformanceCreateComponent} from './components/performance/performance-create/performance-create.component';
import {EventCreateComponent} from './components/events/event-create/event-create.component';
import {EventsComponent} from './components/events/events.component';
import {EventCategory} from './dtos/event/event-category';
import {RegisterComponent} from './components/register/register.component';
import {ConfirmedComponent} from './components/confirmed/confirmed.component';
import {UserProfileComponent} from './components/user-profile/user-profile.component';
import {ChangePasswordComponent} from './components/change-password/change-password.component';
import {ForgotPasswordComponent} from './components/forgot-password/forgot-password.component';
import {PasswordResetComponent} from './components/reset-password/password-reset.component';
import {ArtistComponent} from './components/artist/artist.component';
import {UserProfileEditComponent} from './components/user-profile-edit/user-profile-edit.component';
import {MerchandiseComponent} from './components/merchandise/merchandise.component';
import {ShoppingCartComponent} from './components/shopping-cart/shopping-cart.component';
import {CreateUserComponent} from './components/create-user/create-user.component';
import {AdminComponent} from './components/admin/admin.component';
import {SearchComponent} from './components/search/search.component';
import {AuthGuard} from './guards/auth.guard';
import {LoggedInAuthGuard} from './guards/logged-in-auth-guard';
import {LoggedInAdminAuthGuard} from './guards/logged-in-admin-auth.guard';
import {OrderOverviewComponent} from './components/order-overview/order-overview.component';
import {LoggedInCustomerAuthGuard} from './guards/logged-in-customer-auth.guard';
import {LoggedOutOrCustomerOrVendorAuthGuard} from './guards/logged-out-or-customer-or-vendor-auth.guard';

const routes: Routes = [
  {path: '', redirectTo: 'message', pathMatch: 'full'},
  {
    path: 'hall-plan', component: HallPlanAppLayoutComponent,
    children: [
      {path: ':id', component: HallPlanComponent},
    ]
  },
  {path: 'signup', component: RegisterComponent, canActivate: [LoggedInAuthGuard]},
  {path: 'login', component: LoginComponent, canActivate: [LoggedInAuthGuard]},
  {
    path: 'message', children: [
      {path: '', component: MessageComponent},
      {
        path: 'create',
        component: MessageCreateEditComponent,
        data: {mode: MessageCreateEditMode.create},
        canActivate: [LoggedInAdminAuthGuard]
      },
      {
        path: ':id/edit',
        component: MessageCreateEditComponent,
        data: {mode: MessageCreateEditMode.edit},
        canActivate: [LoggedInAdminAuthGuard]
      },
      {path: ':id', component: MessageDetailsComponent},
    ]
  },
  {path: 'confirm', component: ConfirmedComponent},
  {
    path: 'events', children: [
      {path: 'cinema', component: EventsComponent, data: {category: EventCategory.cinema}},
      {path: 'theatre', component: EventsComponent, data: {category: EventCategory.theatre}},
      {path: 'opera', component: EventsComponent, data: {category: EventCategory.opera}},
      {path: 'concert', component: EventsComponent, data: {category: EventCategory.concert}},
      {path: 'create', component: EventCreateComponent, canActivate: [LoggedInAdminAuthGuard]},
      {path: ':id/edit', component: EventCreateComponent, canActivate: [LoggedInAdminAuthGuard]},
      {path: ':id/add-performance', component: PerformanceCreateComponent, canActivate: [LoggedInAdminAuthGuard]},
      {path: ':id', component: PerformanceComponent},
    ]
  },
  {path: 'search', component: SearchComponent},
  {
    path: 'artist', children: [
      {path: ':id', component: ArtistComponent},
    ]
  },
  {
    path: 'performance', children: [
      {path: ':id', component: PerformanceComponent},
      {
        path: ':hall-plan', component: HallPlanAppLayoutComponent, canActivate: [LoggedOutOrCustomerOrVendorAuthGuard],
        children: [
          {path: ':eid/:pid/:hid', component: HallPlanComponent},
        ]
      },
    ]
  },
  {path: 'user-profile', component: UserProfileComponent, canActivate: [AuthGuard]},
  {path: 'user-profile/edit', component: UserProfileEditComponent, canActivate: [AuthGuard]},
  {path: 'user-profile/change-password', component: ChangePasswordComponent, canActivate: [AuthGuard]},
  {path: 'forgot-password', component: ForgotPasswordComponent},
  {path: 'password-reset', component: PasswordResetComponent},
  {path: 'user-profile/change-password', component: ChangePasswordComponent},
  {
    path: 'admin', children: [
      {path: '', component: AdminComponent},
      {path: 'create', component: CreateUserComponent}
    ], canActivate: [LoggedInAdminAuthGuard]
  },
  {path: 'password-reset', component: PasswordResetComponent},
  {path: 'merchandise', component: MerchandiseComponent,canActivate: [LoggedOutOrCustomerOrVendorAuthGuard]},
  {path: 'shopping-cart', component: ShoppingCartComponent, canActivate: [LoggedOutOrCustomerOrVendorAuthGuard]},
  {path: 'order-overview', component: OrderOverviewComponent, canActivate: [LoggedInCustomerAuthGuard]},
];

@NgModule({
  imports: [RouterModule.forRoot(routes, {useHash: true})],
  exports: [RouterModule]
})
export class AppRoutingModule {
}
