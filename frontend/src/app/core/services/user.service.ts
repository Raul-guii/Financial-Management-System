import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { User } from '../../models/user.model';

@Injectable({
  providedIn: 'root'
})
export class UserService {

  private api = 'http://localhost:8080/users';

  constructor(private http: HttpClient) {}

  getAll(): Observable<User[]> {
    return this.http.get<User[]>(this.api);
  }

  getById(id: number): Observable<User> {
    return this.http.get<User>(`${this.api}/${id}`);
  }

  create(data: any): Observable<User> {
    return this.http.post<User>(this.api, data);
  }

  update(id: number, data: any): Observable<User> {
    return this.http.put<User>(`${this.api}/${id}`, data);
  }

  deactivate(id: number) {
    return this.http.patch(`${this.api}/${id}/deactivate`, {});
  }

  delete(id: number) {
    return this.http.delete(`${this.api}/${id}`);
  }
}