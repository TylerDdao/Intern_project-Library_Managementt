import { ChangeDetectorRef, Component, EventEmitter, Inject, Input, Output, PLATFORM_ID } from '@angular/core';
import { TranslateModule } from '@ngx-translate/core';
import { Comment } from '../../models/comment';
import { formatTime } from '../../util/format-number';
import { Post } from '../../models/post';
import { CommentService } from '../../services/comment-service/comment-service';
import { HttpErrorResponse } from '@angular/common/http';
import { isPlatformBrowser } from '@angular/common';
import { LoadingComponent } from '../loading-component/loading-component';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';

@Component({
  selector: 'app-comment-box-component',
  imports: [TranslateModule, LoadingComponent, ReactiveFormsModule],
  templateUrl: './comment-box-component.html',
  styleUrl: './comment-box-component.css',
})
export class CommentBoxComponent {
  @Input({required: true}) post!:Post
  @Output() onClose = new EventEmitter<void>();

  constructor(
    private commentService: CommentService,
    private cdr: ChangeDetectorRef,
    @Inject(PLATFORM_ID) private platformId: Object
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
    console.log("Delete" + " " + comment.content);
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

  getFormattedCreatedAt(time: string): string {
    return formatTime(time);
  }
}
