import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../environments/environment';


@Injectable({
  providedIn: 'root'
})
export class ApiService {

  private apiUrl = environment.apiUrl;

  constructor(private http: HttpClient) { }


  signIn(token: string, email: string): Observable<any> {
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`  // Attach the JWT token
    });

    const params = new HttpParams()
                       .set('token', token)
                       .set('email', email);


    return this.http.post(this.apiUrl + "/users", "", { headers: headers , params: params});
  }
}
