import { Component, EventEmitter, Input, Output } from '@angular/core';
import { TranslateModule } from '@ngx-translate/core';
import { Comment } from '../../models/comment';
import { formatTime } from '../../util/format-number';

@Component({
  selector: 'app-comment-box-component',
  imports: [TranslateModule],
  templateUrl: './comment-box-component.html',
  styleUrl: './comment-box-component.css',
})
export class CommentBoxComponent {
  @Input({required: true}) comments!:Comment[]
  @Output() onClose = new EventEmitter<void>();

  close(): void {
    this.onClose.emit();
  }

  getFormattedCreatedAt(time: string): string {
    return formatTime(time);
  }
}
