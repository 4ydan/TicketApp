import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {HttpClientModule} from '@angular/common/http';

import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {HeaderComponent} from './components/header/header.component';
import {FooterComponent} from './components/footer/footer.component';
import {LoginComponent} from './components/login/login.component';
import {MessageComponent} from './components/message/message.component';
import {RegisterComponent} from './components/register/register.component';
import {ConfirmedComponent} from './components/confirmed/confirmed.component';
import {NgbCollapseModule, NgbModule} from '@ng-bootstrap/ng-bootstrap';
import {httpInterceptorProviders} from './interceptors';
import {MessageDetailsComponent} from './components/message/details/message-details.component';
import {ConfirmDeleteDialogComponent} from './components/confirm-delete-dialog/confirm-delete-dialog.component';
import {HallPlanComponent} from './components/hall-plan/hall-plan.component';
import {HallPlanAppLayoutComponent} from './components/hall-plan/hall-plan-app-layout/hall-plan-app-layout.component';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {ToastrModule} from 'ngx-toastr';
import {NgxBootstrapIconsModule} from 'ngx-bootstrap-icons';
import {EventsComponent} from './components/events/events.component';
import {PerformanceComponent} from './components/performance/performance.component';
import {HeaderSearchComponent} from './components/header-search/header-search.component';
import {UserProfileComponent} from './components/user-profile/user-profile.component';
import {UserProfileEditComponent} from './components/user-profile-edit/user-profile-edit.component';
import {ChangePasswordComponent} from './components/change-password/change-password.component';
import {CommonModule} from '@angular/common';
import {ArtistComponent} from './components/artist/artist.component';
import {PasswordResetComponent} from './components/reset-password/password-reset.component';
import {ForgotPasswordComponent} from './components/forgot-password/forgot-password.component';
import {MessageCreateEditComponent} from './components/message/message-create-edit/message-create-edit.component';
import {AutocompleteComponent} from './components/autocomplete/autocomplete.component';
import {MerchandiseComponent} from './components/merchandise/merchandise.component';
import {ShoppingCartComponent} from './components/shopping-cart/shopping-cart.component';
import {SearchComponent} from './components/search/search.component';
import {CreateUserComponent} from './components/create-user/create-user.component';
import {AdminComponent} from './components/admin/admin.component';
import {OrderOverviewComponent} from './components/order-overview/order-overview.component';
import {NgxPaginationModule} from 'ngx-pagination';
import {OrderInvoiceComponent} from './components/invoices/order-invoice/order-invoice.component';
import {CancellationInvoiceComponent} from './components/invoices/cancellation-invoice/cancellation-invoice.component';
import {EventCreateComponent} from './components/events/event-create/event-create.component';
import {PerformanceCreateComponent} from './components/performance/performance-create/performance-create.component';

@NgModule({
  declarations: [
    AppComponent,
    HeaderComponent,
    FooterComponent,
    LoginComponent,
    MessageComponent,
    MessageCreateEditComponent,
    HallPlanComponent,
    HallPlanAppLayoutComponent,
    RegisterComponent,
    ConfirmedComponent,
    EventsComponent,
    PerformanceComponent,
    HeaderSearchComponent,
    SearchComponent,
    UserProfileComponent,
    UserProfileEditComponent,
    ChangePasswordComponent,
    PasswordResetComponent,
    ForgotPasswordComponent,
    MessageDetailsComponent,
    ConfirmDeleteDialogComponent,
    ArtistComponent,
    MerchandiseComponent,
    ShoppingCartComponent,
    CreateUserComponent,
    AdminComponent,
    EventCreateComponent,
    AutocompleteComponent,
    PerformanceComponent,
    PerformanceCreateComponent,
    OrderOverviewComponent,
    OrderInvoiceComponent,
    CancellationInvoiceComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    ReactiveFormsModule,
    HttpClientModule,
    NgbModule,
    CommonModule,
    FormsModule,
    ToastrModule.forRoot({
      timeOut: 7000,
    }),
    BrowserAnimationsModule,
    NgxBootstrapIconsModule,
    NgbCollapseModule,
    NgxPaginationModule,
  ],
  providers: [httpInterceptorProviders],
  bootstrap: [AppComponent]
})
export class AppModule {
}
