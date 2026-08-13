import { TranslateService } from '@ngx-translate/core';
import { HttpErrorResponse } from '@angular/common/http';

export function errorNoti(err: HttpErrorResponse, translate: TranslateService) {
  translate.get("error.An-error-has-occurred").subscribe((message: string) => {
    alert(`${message}\n${err.error?.code}: ${err.error?.message}`);
  });
  console.error(err.error?.code + ": " + err.error?.message);
}