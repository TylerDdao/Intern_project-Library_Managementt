import { Component, EventEmitter, Input, Output } from '@angular/core';
import { TranslateModule } from '@ngx-translate/core';
import { LanguageService } from '../../services/language-service/language-service';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Role } from '../../models/role';

@Component({
  selector: 'app-new-user-form',
  imports: [TranslateModule, ReactiveFormsModule],
  templateUrl: './new-user-form.html',
  styleUrl: './new-user-form.css',
})
export class NewUserForm {
  @Input() roles: Role[] = [];
  @Output() onClose = new EventEmitter<void>();
  constructor(
  ){}

  newUserForm = new FormGroup({
    username: new FormControl('', Validators.required),
    password: new FormControl('', Validators.required),
    fullName: new FormControl('', Validators.required),
    phoneNumber: new FormControl('', Validators.required),
    email: new FormControl('', Validators.required),
    address: new FormControl(''),
    role: new FormControl('4', Validators.required)
  });

  onSubmit(){
    console.log("Submit")
  }

  close(): void {
    this.onClose.emit();
  }
}
