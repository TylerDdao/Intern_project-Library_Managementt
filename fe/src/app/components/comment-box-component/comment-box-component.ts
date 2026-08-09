import { ChangeDetectorRef, Component, EventEmitter, Inject, Input, Output, PLATFORM_ID } from '@angular/core';
import { TranslateModule, TranslateService } from '@ngx-translate/core';
import { Comment } from '../../models/comment';
import { formatTime } from '../../util/format-number';
import { Post } from '../../models/post';
import { CommentService } from '../../services/comment-service/comment-service';
import { HttpErrorResponse } from '@angular/common/http';
import { isPlatformBrowser } from '@angular/common';
import { LoadingComponent } from '../loading-component/loading-component';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { LanguageService } from '../../services/language-service/language-service';
import { errorNoti } from '../../util/error-notification';

@Component({
  selector: 'app-comment-box-component',
  imports: [TranslateModule, LoadingComponent, ReactiveFormsModule],
  templateUrl: './comment-box-component.html',
  styleUrl: './comment-box-component.css',
})
export class CommentBoxComponent {
  @Input({required: true}) post!:Post
  @Output() onClose = new EventEmitter<void>();
  @Output() onChange = new EventEmitter<number>();

  constructor(
    private commentService: CommentService,
    private cdr: ChangeDetectorRef,
    @Inject(PLATFORM_ID) private platformId: Object,
    private langService: LanguageService,
    private translate: TranslateService
  ){

  }

  comments:Comment[] =[]
  isLoadingComments:boolean = true;

  fetchComments(){
    this.isLoadingComments = true;
    this.commentService.getComments(this.post.id).subscribe({
      next: (data:any)=>{
        if(data.code == "200"){
          this.comments = data.data.content;
          this.isLoadingComments = false;
          this.cdr.markForCheck();
        }
      },
      error:(err:HttpErrorResponse)=>{
        console.error(err.error.code + ": " + err.error.message)
      }
    })
  }

  newCommentForm = new FormGroup({
    content: new FormControl('', Validators.required),
  });

  handleDeleteComment(comment:Comment){
    const message = this.translate.instant("form.Confirm-delete")
    const option = confirm(message+"?")
    if(!option) return
    this.commentService.deleteComment(comment.id).subscribe({
      next: (data:any)=>{
        if(data.code == "200"){
          this.fetchComments();
          this.cdr.markForCheck();
          this.change(-1);
        }
      },
      error:(err:HttpErrorResponse)=>{
        errorNoti(err,this.translate)
      }
    })
  }

  onSubmit(){
    if(this.newCommentForm.valid){
      const {content} = this.newCommentForm.value;
      if(content){
        this.commentService.createComment(content,this.post.id).subscribe({
          next: (data:any)=>{
            if(data.code == "200"){
              this.fetchComments();
              this.newCommentForm.patchValue({
                content: ""
              })
              this.cdr.markForCheck()
              this.change(1)
            }
          },
          error: (err:HttpErrorResponse)=>{
            console.error(err.error.code+": "+err.error.message);
          }
        })
      }
    }
  }

  ngOnInit(){
    if(isPlatformBrowser(this.platformId)){
      this.fetchComments()
    }
  }

  close(): void {
    this.onClose.emit();
  }

  change(commentCount:number):void{
    this.onChange.emit(commentCount);
  }

  getFormattedCreatedAt(time: string): string {
    return formatTime(time, this.langService.currentLang);
  }
}
