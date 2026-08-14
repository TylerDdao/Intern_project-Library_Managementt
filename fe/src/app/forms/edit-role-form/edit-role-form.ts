import { ChangeDetectorRef, Component, EventEmitter, Input, OnChanges, Output, SimpleChanges } from '@angular/core';
import { Role } from '../../models/role';
import { TranslateModule, TranslateService } from '@ngx-translate/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Feature } from '../../models/feature';
import { RoleService } from '../../services/role-service/role-service';
import { HttpErrorResponse } from '@angular/common/http';
import { errorNoti } from '../../util/error-notification';
import { forkJoin } from 'rxjs';
import { arraysHaveSameElements } from '../../util/array-compare';
import { LoadingComponent } from "../../components/loading-component/loading-component";


@Component({
  selector: 'app-edit-role-form',
  imports: [TranslateModule, ReactiveFormsModule, LoadingComponent],
  templateUrl: './edit-role-form.html',
  styleUrl: './edit-role-form.css',
})
export class EditRoleForm implements OnChanges {
  @Input() role!: Role
  @Input() features!: Feature[]
  @Output() onClose = new EventEmitter<void>();
  @Output() onChange = new EventEmitter<boolean>();

  assignedFeatures: Feature[] = []
  unassignedFeatures: Feature[] = []

  addedFeatures: Feature[] = [];
  removeFeatures: Feature[] =[];

  isSaved: boolean = false;

  isProcessing:boolean = false
  isDeleting:boolean = false
  isLoading:boolean = false;

  constructor(
    private cdr: ChangeDetectorRef,
    private roleService:RoleService,
    private translate: TranslateService
  ) {}

  handleUnassignedAll(){
    this.removeFeatures = this.assignedFeatures;
  }
  handleResetAssignedFeatures(){
    this.removeFeatures = []
  }

  handleResetUnassignedFeatures(){
    this.addedFeatures = []
  }

  handleAssignedAll(){
    this.addedFeatures = this.unassignedFeatures;
  }

  ngOnChanges(changes: SimpleChanges) {
    if ((changes['features'] || changes['role']) && this.features && this.role?.features) {
      this.assignedFeatures = this.role.features
      this.unassignedFeatures = this.features.filter(
        feature => !this.role.features.some(f => f.id === feature.id)
      );
      this.cdr.markForCheck();
    }
    if(changes["role"] && this.role){
      this.newRoleForm.patchValue({
        roleName: this.role.name,
        defaultRole: this.role.default
      })
  }
}

  newRoleForm = new FormGroup({
    roleName: new FormControl('', Validators.required),
    defaultRole: new FormControl<boolean>(false)
  });

  handleDeleteRole(){
    const message = this.translate.instant("form.Confirm-delete")
    const option = confirm(message + "?")
    if(option){
      this.isDeleting=true
      this.isLoading = true
      this.roleService.deleteRole(this.role).subscribe({
        next: (data: any)=>{
          if(data.code == "200"){
            this.save(true)
          }
          this.isDeleting=false
          this.isLoading = false
        },
        error:(err:HttpErrorResponse)=>{
          errorNoti(err, this.translate)
          this.save(false);
          this.isDeleting=false
          this.isLoading = false
        }
      })
    }
  }

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
    if(!this.role) return;

    const requests = [];

    const { roleName, defaultRole } = this.newRoleForm.value;
    this.isProcessing = true;
    this.isLoading = true

    if(roleName && this.role.name !== roleName || this.role.default !== defaultRole){
      this.role.default = defaultRole ?? false;

      requests.push(
        this.roleService.updateRole(this.role)
      );
    }

    if(this.addedFeatures.length > 0){
      requests.push(
        this.roleService.assignFeature(this.role, this.addedFeatures)
      );
    }

    if(this.removeFeatures.length > 0){
      requests.push(
        this.roleService.unassignFeature(this.role, this.removeFeatures)
      );
    }

    if(requests.length === 0){
      this.save(false);
      this.isProcessing = false;
      this.isLoading = false
      return;
    }

    forkJoin(requests).subscribe({
      next: (responses:any[]) => {
        const success = responses.every(res => res.code === "200");
        this.save(success);
        this.isProcessing=false;
        this.isLoading = false
      },
      error:(err:HttpErrorResponse)=>{
        errorNoti(err, this.translate);
        // this.save(false);
        this.isProcessing=false;
        this.isLoading = false
      }
    });
  }


  close(){
    this.onClose.emit();
  }

  save(result: boolean){
    this.onChange.emit(result)
  }
}
