import { Component } from '@angular/core';
import { Encabezado } from './Core/Layout/encabezado/encabezado';
import { ListaPacientes } from './Pacientes/lista-pacientes/lista-pacientes';
import { TarjetaPaciente } from './Pacientes/tarjeta-paciente/tarjeta-paciente';

@Component({
  selector: 'app-root',
  imports: [Encabezado, ListaPacientes, TarjetaPaciente],
  templateUrl: './app.html',
  styleUrl: './app.css'
})

export class App {
  
}
