import { Directive, HostListener, Self } from '@angular/core';
import { NgControl } from '@angular/forms';

@Directive({
  selector: '[phoneMask]',
  standalone: true,
})
export class PhoneMaskDirective {
  constructor(@Self() private ngControl: NgControl) {}

  @HostListener('input', ['$event'])
  onInput(event: Event): void {
    const input = event.target as HTMLInputElement;
    const raw = input.value.replace(/\D/g, '');
    this.ngControl.control?.setValue(this.mask(raw), { emitEvent: false });
  }

  @HostListener('blur')
  onBlur(): void {
    const raw = (this.ngControl.value ?? '').replace(/\D/g, '');
    this.ngControl.control?.setValue(this.mask(raw), { emitEvent: false });
  }

  private mask(v: string): string {
    v = v.slice(0, 11);
    if (v.length > 10) return `(${v.slice(0,2)}) ${v.slice(2,7)}-${v.slice(7)}`;
    if (v.length > 6)  return `(${v.slice(0,2)}) ${v.slice(2,6)}-${v.slice(6)}`;
    if (v.length > 2)  return `(${v.slice(0,2)}) ${v.slice(2)}`;
    if (v.length > 0)  return `(${v}`;
    return v;
  }
}