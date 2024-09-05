import { Component } from '@angular/core';
import { RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { Amplify } from 'aws-amplify';
import { AmplifyAuthenticatorModule } from '@aws-amplify/ui-angular';
import {MatNativeDateModule} from '@angular/material/core';


import { CommonModule } from '@angular/common';

const awsconfig = require('../assets/aws-exports').default;

Amplify.configure(awsconfig);


@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet,RouterLink, RouterLinkActive,AmplifyAuthenticatorModule, CommonModule, MatNativeDateModule],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})

export class AppComponent {
  title = 'frontend';

  isLoggedIn(): boolean {
    return !!localStorage.getItem('token'); 
  }

}
