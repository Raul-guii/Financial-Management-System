import { Directive, HostListener, Self } from '@angular/core';
import { NgControl } from '@angular/forms';

@Directive({
  selector: '[cepMask]',
  standalone: true,
})
export class CepMaskDirective {
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
    v = v.slice(0, 8);
    if (v.length > 5) return `${v.slice(0,5)}-${v.slice(5)}`;
    return v;
  }
}