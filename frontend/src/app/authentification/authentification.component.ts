import { Component, ViewEncapsulation } from '@angular/core';
import { Amplify } from 'aws-amplify';
import { AmplifyAuthenticatorModule, AuthenticatorService } from '@aws-amplify/ui-angular';
import { Hub } from 'aws-amplify/utils';
import { fetchAuthSession, fetchUserAttributes } from "aws-amplify/auth";
import { ApiService } from '../api.service';
//import '@aws-amplify/ui/dist/styles.css';



const awsconfig = require('../../assets/aws-exports').default;

@Component({
  selector: 'app-authentification',
  standalone: true,
  imports: [AmplifyAuthenticatorModule],
  templateUrl: './authentification.component.html',
  styleUrl: './authentification.component.css',
  encapsulation: ViewEncapsulation.None 
})
export class AuthentificationComponent {
  
  constructor(public authenticator: AuthenticatorService, private apiService: ApiService) {
    this.initializeAuthListener();
  }

  initializeAuthListener() {
    Hub.listen('auth', (data) => {
      const payload = data.payload;

      // Detect signIn event (redirection after signUp or simple signIn)
      if (payload.event === 'signedIn') {
        this.getToken()
          .then(token => {
            if (token) {
              localStorage.setItem('token', token)
              this.fetchUserAttributes()
                .then(userAttributes => {
                  console.log(userAttributes.email);

                  this.apiService.signIn(token, userAttributes.email).subscribe({
                    next: (response) => {
                      localStorage.setItem("cognitoToken", response.token);
                    },
                    error: (err) => console.error('API error:', err.message)
                  });
                })
                .catch(err => console.error('Error fetching user attributes:', err));
            }
          })
          .catch(err => console.error('Error getting token:', err));
      }

      if (payload.event === 'signedOut') {
        localStorage.removeItem('token')
      }
    });
  }

 async getToken(){
  return (await fetchAuthSession()).tokens?.accessToken.toString();
 }

 async fetchUserAttributes(): Promise<any> {
  try {
    const user = await fetchUserAttributes();
    return user; // assuming `attributes` contains the user attributes including email
  } catch (error) {
    console.error('Error retrieving user attributes:', error);
    throw error;
  }
}

}
