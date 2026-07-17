import { ChangeDetectorRef, Component, EventEmitter, Input, Output } from '@angular/core';
import { NgClass } from '@angular/common';
import { TranslateModule } from '@ngx-translate/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { LanguageService } from '../../services/language-service/language-service';
import { Feature } from '../../models/feature';

@Component({
  selector: 'app-new-role-form',
  imports: [TranslateModule, ReactiveFormsModule],
  templateUrl: './new-role-form.html',
  styleUrl: './new-role-form.css',
})
export class NewRoleForm {
  @Input() features!: Feature[]
  @Output() onClose = new EventEmitter<void>();

  constructor(
    private langService: LanguageService,
    private cdr: ChangeDetectorRef
  ) {}

  addedFeatures: Feature[] = [];

  newRoleForm = new FormGroup({
    roleName: new FormControl('', Validators.required),
  });

  handleToggleFeature(choosenFeature: Feature) {
    if (this.addedFeatures.includes(choosenFeature)) {
      this.addedFeatures = this.addedFeatures.filter(feature => feature !== choosenFeature);
    } else {
      this.addedFeatures.push(choosenFeature);
    }
    this.cdr.markForCheck();
  }

  onSubmit(){
    console.log("Submit")
  }

  close(){
    this.onClose.emit();
  }
}
