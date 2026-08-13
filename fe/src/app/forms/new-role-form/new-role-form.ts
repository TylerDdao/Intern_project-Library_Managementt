import { ChangeDetectorRef, Component, EventEmitter, Input, Output } from '@angular/core';
import { TranslateModule, TranslateService } from '@ngx-translate/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { LanguageService } from '../../services/language-service/language-service';
import { Feature } from '../../models/feature';
import { forkJoin } from 'rxjs';
import { errorNoti } from '../../util/error-notification';
import { HttpErrorResponse } from '@angular/common/http';
import { RoleService } from '../../services/role-service/role-service';
import { Role } from '../../models/role';


@Component({
  selector: 'app-new-role-form',
  imports: [TranslateModule, ReactiveFormsModule],
  templateUrl: './new-role-form.html',
  styleUrl: './new-role-form.css',
})
export class NewRoleForm {
  @Input() features!: Feature[]
  @Output() onClose = new EventEmitter<void>();
  @Output() onChange = new EventEmitter<boolean>();

  constructor(
    private roleService: RoleService,
    private cdr: ChangeDetectorRef,
    private translate: TranslateService
  ) {}

  addedFeatures: Feature[] = [];

  isSaved: boolean = false;

  newRoleForm = new FormGroup({
    roleName: new FormControl('', Validators.required),
    defaultRole: new FormControl<boolean>(false)
  });

  handleAssignedAll(){
    this.addedFeatures = this.features;
  }
  handleResetAssignedFeatures(){
    this.addedFeatures = [];
  }

  handleToggleFeature(choosenFeature: Feature) {
    if (this.addedFeatures.includes(choosenFeature)) {
      this.addedFeatures = this.addedFeatures.filter(feature => feature !== choosenFeature);
    } else {
      this.addedFeatures.push(choosenFeature);
    }
    this.cdr.markForCheck();
  }

  onSubmit(){
    const { roleName, defaultRole } = this.newRoleForm.value;

    if(!roleName) return;

    const role: Role = {
      name: roleName.startsWith("ROLE_")
        ? roleName.replace("ROLE_", "") : roleName,
      default: defaultRole ?? false,
      features: []
    };

    this.roleService.createRole(role).subscribe({
      next:(data:any)=>{
        if(data.code === "200"){
          const createdRole: Role = data.data;

          if(this.addedFeatures.length > 0){
            this.roleService.assignFeature(
              createdRole,
              this.addedFeatures
            ).subscribe({
              next:(featureData:any)=>{
                if(featureData.code === "200"){
                  this.save(true);
                }
              },
              error:(err:HttpErrorResponse)=>{
                errorNoti(err, this.translate);
                this.save(false);
              }
            });
          }
          else {
            this.save(true);
          }
        }
      },
      error:(err:HttpErrorResponse)=>{
        errorNoti(err, this.translate);
        this.save(false);
      }
    });
  }

  save(result: boolean){
    this.onChange.emit(result)
  }

  close(){
    this.onClose.emit();
  }
}
