import { ChangeDetectorRef, Component, EventEmitter, Input, OnChanges, Output, SimpleChanges } from '@angular/core';
import { Role } from '../../models/role';
import { TranslateModule, TranslateService } from '@ngx-translate/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { LanguageService } from '../../services/language-service/language-service';
import { Feature } from '../../models/feature';

@Component({
  selector: 'app-edit-role-form',
  imports: [TranslateModule, ReactiveFormsModule],
  templateUrl: './edit-role-form.html',
  styleUrl: './edit-role-form.css',
})
export class EditRoleForm implements OnChanges {
  @Input() role!: Role
  @Input() features!: Feature[]
  @Output() onClose = new EventEmitter<void>();

  assignedFeatures: Feature[] = []
  unassignedFeatures: Feature[] = []
  addedFeatures: Feature[] = [];
  removeFeatures: Feature[] =[];

  constructor(
    private langService: LanguageService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnChanges(changes: SimpleChanges) {
  if ((changes['features'] || changes['role']) && this.features && this.role?.features) {
    this.assignedFeatures = this.role.features
    this.unassignedFeatures = this.features.filter(
      feature => !this.role.features.some(f => f.id === feature.id)
    );
    this.cdr.markForCheck();
  }
  if(changes['roleName'] || changes["role"] && this.role){
    this.newRoleForm.patchValue({
      roleName: this.role.name
    })
  }
}

  newRoleForm = new FormGroup({
    roleName: new FormControl('', Validators.required),
  });

  handleToggleFeature(choosenFeature: Feature) {
    if(this.assignedFeatures.includes(choosenFeature)){
      if (this.removeFeatures.includes(choosenFeature)) {
        this.removeFeatures = this.removeFeatures.filter(feature => feature !== choosenFeature);
      } else {
        this.removeFeatures.push(choosenFeature);
      }
    }
    else{
      if (this.addedFeatures.includes(choosenFeature)) {
        this.addedFeatures = this.addedFeatures.filter(feature => feature !== choosenFeature);
      } else {
        this.addedFeatures.push(choosenFeature);
      }
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
