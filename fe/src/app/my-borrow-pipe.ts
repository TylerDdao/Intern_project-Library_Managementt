import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'myBorrow',
})
export class MyBorrowPipe implements PipeTransform {
  transform(value: unknown, ...args: unknown[]): unknown {
    return null;
  }
}
