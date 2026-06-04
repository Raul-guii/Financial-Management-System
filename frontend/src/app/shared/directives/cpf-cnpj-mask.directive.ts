import { Directive, HostListener, Input, OnChanges, SimpleChanges, Self } from '@angular/core';
import { NgControl } from '@angular/forms';

@Directive({
  selector: '[cpfCnpjMask]',
  standalone: true,
})
export class CpfCnpjMaskDirective implements OnChanges {
  @Input('cpfCnpjMask') personType: 'PERSON' | 'COMPANY' | string = '';

  constructor(@Self() private ngControl: NgControl) {}

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['personType']) {
      const raw = this.stripMask(this.ngControl.value ?? '');
      this.applyMask(raw);
    }
  }

  @HostListener('input', ['$event'])
  onInput(event: Event): void {
    const input = event.target as HTMLInputElement;
    const raw = this.stripMask(input.value);
    this.applyMask(raw);
  }

  @HostListener('blur')
  onBlur(): void {
    const raw = this.stripMask(this.ngControl.value ?? '');
    this.applyMask(raw);
  }

  private applyMask(digits: string): void {
    const masked = this.personType === 'PERSON'
      ? this.maskCpf(digits)
      : this.maskCnpj(digits);
    this.ngControl.control?.setValue(masked, { emitEvent: false });
  }

  private maskCpf(v: string): string {
    v = v.slice(0, 11);
    if (v.length > 9) return `${v.slice(0,3)}.${v.slice(3,6)}.${v.slice(6,9)}-${v.slice(9)}`;
    if (v.length > 6) return `${v.slice(0,3)}.${v.slice(3,6)}.${v.slice(6)}`;
    if (v.length > 3) return `${v.slice(0,3)}.${v.slice(3)}`;
    return v;
  }

  private maskCnpj(v: string): string {
    v = v.slice(0, 14);
    if (v.length > 12) return `${v.slice(0,2)}.${v.slice(2,5)}.${v.slice(5,8)}/${v.slice(8,12)}-${v.slice(12)}`;
    if (v.length > 8)  return `${v.slice(0,2)}.${v.slice(2,5)}.${v.slice(5,8)}/${v.slice(8)}`;
    if (v.length > 5)  return `${v.slice(0,2)}.${v.slice(2,5)}.${v.slice(5)}`;
    if (v.length > 2)  return `${v.slice(0,2)}.${v.slice(2)}`;
    return v;
  }

  private stripMask(value: string): string {
    return value.replace(/\D/g, '');
  }
}