import { Component } from '@angular/core';
import { NavbarComponent } from '../../components/navbar/navbar';
import { UserCard } from '../../components/user-card/user-card';
import { User } from '../../models/user';
import { UserService } from '../../services/user-service/user-service';

@Component({
  selector: 'app-users-management',
  imports: [NavbarComponent, UserCard],
  templateUrl: './users-management.html',
  styleUrl: './users-management.css',
})
export class UsersManagement {
  user: User = {
    id: 1,
    fullName: "Tyler Dao",
    username: "tyler",
    email: "baonam6a3@gmail.com",
    phoneNumber: "0964243434",
    role: "ROLE_ROOT"
  }

  constructor(
    private userService: UserService
  ){}

  ngOnInit(){
    this.userService.getAllRoles().subscribe({
      next: (data: any) => {
        if( data.code =="200"){
          console.log(data.data.content)
        }
      },
    })
  }
}
