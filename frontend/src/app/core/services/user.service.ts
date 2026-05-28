import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { UserResponse } from '../../models/users/user-response.model';
import { UserCreateRequest } from '../../models/users/user-create.model';
import { UserUpdateRequest } from '../../models/users/user-update.model';
import { Page } from '../../models/pages/page.model';


@Injectable({ providedIn: 'root' })
export class UserService {
  private api = `${environment.apiUrl}/users`;

  constructor(private http: HttpClient) {}

  getAll(page: number = 0, size: number = 20, search: string = ''): Observable<Page<UserResponse>> {
    const params = search
      ? `?page=${page}&size=${size}&sort=name&search=${encodeURIComponent(search)}`
      : `?page=${page}&size=${size}&sort=name`;
    return this.http.get<Page<UserResponse>>(`${this.api}${params}`);
  }

  getById(id: number): Observable<UserResponse> {
    return this.http.get<UserResponse>(`${this.api}/${id}`);
  }

  create(data: UserCreateRequest): Observable<UserResponse> {
    return this.http.post<UserResponse>(this.api, data);
  }

  update(id: number, data: UserUpdateRequest): Observable<UserResponse> {
    return this.http.put<UserResponse>(`${this.api}/${id}`, data);
  }

  deactivate(id: number): Observable<void> {
    return this.http.patch<void>(`${this.api}/${id}/deactivate`, {});
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.api}/${id}`);
  }
}