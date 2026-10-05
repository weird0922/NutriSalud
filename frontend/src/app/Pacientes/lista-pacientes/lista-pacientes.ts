import { Component } from '@angular/core';
import { TarjetaPaciente } from '../tarjeta-paciente/tarjeta-paciente';
import { Paciente } from '../../Modelos/Paciente';
import { clasificarAnemia, tieneAnemia } from '../../reglas/anemia';
import { DatePipe } from '@angular/common';

@Component({
  selector: 'app-lista-pacientes',
  imports: [ListaPacientes, TarjetaPaciente, DatePipe],
  templateUrl: './lista-pacientes.html',
  styleUrl: './lista-pacientes.css',
})
export class ListaPacientes {
  titulo = 'Pacientes en seguimiento'; // lo lee la interpolacion
  filtro = ''; // lo escribe el event binding
  soloAnemia = false;

    pacientes: Paciente[] = [
    { id: 1, nombres: 'Mateo',
      apellidos: 'Rios Vega', dni: '71234567',
      edadMeses: 18, hemoglobina: 9.8, altitudMsnm: 65,
      fechaUltimoControl: '2026-09-02' },
    { id: 2, nombres: 'Luana',
      apellidos: 'Quispe Soto', dni: '72345678',
      edadMeses: 30, hemoglobina: 11.4, altitudMsnm: 34,
      fechaUltimoControl: '2026-09-05' },
    { id: 3, nombres: 'Thiago',
      apellidos: 'Alva Mendoza', dni: '73456789',
      edadMeses: 24, hemoglobina: 6.9, altitudMsnm: 2641,
      fechaUltimoControl: '2026-08-28' },
    ];

get visibles(): Paciente[] { // un getter: se recalcula cuando algo cambia
  const texto = this.filtro.toLowerCase().trim();
  return this.pacientes
    .filter(p => !this.soloAnemia || this.conAnemia(p))
    .filter(p => (p.nombres + ' ' + p.apellidos).toLowerCase().includes(texto));
}
private conAnemia(p: Paciente): boolean { // la MISMA regla que pinta la tarjeta
  return tieneAnemia(clasificarAnemia(p.hemoglobina, p.edadMeses, p.altitudMsnm));
}
filtrar(evento: Event): void { this.filtro = (evento.target as HTMLInputElement).value; }
alternarSoloAnemia(): void { this.soloAnemia = !this.soloAnemia; }

seleccionado?: Paciente; // ? = puede no haber
verDetalle(p: Paciente): void { this.seleccionado = p; }

}
