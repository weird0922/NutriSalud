import { Component, Input, Output, EventEmitter } from '@angular/core';
import { clasificarAnemia, NivelAnemia} from '../../reglas/anemia';
import { DatePipe, DecimalPipe } from '@angular/common';
import { Paciente } from '../../Modelos/Paciente';

@Component({
  selector: 'app-tarjeta-paciente',
  imports: [DatePipe, DecimalPipe],
  templateUrl: './tarjeta-paciente.html',
  styleUrl: './tarjeta-paciente.css',
})
export class TarjetaPaciente {
  @Input({ required: true }) paciente!: Paciente;
  
  @Output() seleccionar = new EventEmitter<Paciente>();
  
  avisarSeleccion(): void {
  this.seleccionar.emit(this.paciente);
  }

  nivelAnemia(): NivelAnemia { // ahora lee del @Input
  const p = this.paciente;
  return clasificarAnemia(p.hemoglobina, p.edadMeses, p.altitudMsnm);
  }
}
