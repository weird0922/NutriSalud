import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TarjetaPaciente } from './tarjeta-paciente';

describe('TarjetaPaciente', () => {
  let component: TarjetaPaciente;
  let fixture: ComponentFixture<TarjetaPaciente>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TarjetaPaciente],
    }).compileComponents();

    fixture = TestBed.createComponent(TarjetaPaciente);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
