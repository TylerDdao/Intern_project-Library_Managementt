import { TranslateService } from '@ngx-translate/core';
import { HttpErrorResponse } from '@angular/common/http';

export function errorNoti(err: HttpErrorResponse, translate: TranslateService) {
  const message = translate.instant("error.An-error-has-occured");
  alert(`${message} ${err.error?.code}: ${err.error?.message}`);
}