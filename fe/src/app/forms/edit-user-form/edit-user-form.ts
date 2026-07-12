import { Component, EventEmitter, Input, OnChanges, Output, SimpleChanges } from '@angular/core';
import { TranslateModule } from '@ngx-translate/core';
import { LanguageService } from '../../services/language-service/language-service';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Role } from '../../models/role';
import { User } from '../../models/user';

@Component({
  selector: 'app-edit-user-form',
  imports: [TranslateModule, ReactiveFormsModule],
  templateUrl: './edit-user-form.html',
  styleUrl: './edit-user-form.css',
})

export class EditUserForm implements OnChanges {
  @Input() roles: Role[] = [];
  @Input() user!: User;
  @Output() onClose = new EventEmitter<void>();

  constructor(private langService: LanguageService) {}

  newUserForm = new FormGroup({
    username: new FormControl('', Validators.required),
    fullName: new FormControl('', Validators.required),
    phoneNumber: new FormControl('', Validators.required),
    email: new FormControl('', Validators.required),
    address: new FormControl(''),
    role: new FormControl<number | null>(null, Validators.required)
  });

  ngOnChanges(changes: SimpleChanges) {
    if (changes['user'] && this.user) {
      this.newUserForm.patchValue({
        username: this.user.username,
        fullName: this.user.fullName,
        phoneNumber: this.user.phoneNumber,
        email: this.user.email,
        address: this.user.address,
        role: this.user.role?.id ?? null
      });
    }
  }

  onSubmit(){
    console.log("Submit")
    console.log(this.newUserForm.value)
  }

  close(): void {
    this.onClose.emit();
  }
}