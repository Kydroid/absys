import { Injectable } from '@angular/core';
import {ApiService} from "./api.service";
import {User} from "../class/user";

@Injectable({
  providedIn: 'root'
})
export class UserService {

  private currentUser: User;
  constructor(
    private apiService: ApiService
  ) { }

  public async login(id: string): Promise<User> {
      const user = await this.apiService.post('user/login', id);
      this.currentUser = user;
      return user;
  }

  public getCurrentUser() {
    return this.currentUser;
  }

  public async register(user: User): Promise<User> {
    const newUser = await this.apiService.post('user/register', user);
    this.currentUser = newUser;
    return newUser;
  }

  async findAll() {
    return this.apiService.get('user/');
  }

  async workflow(id: any) {
    return this.apiService.get('user/workflow/' + id);
  }
}
