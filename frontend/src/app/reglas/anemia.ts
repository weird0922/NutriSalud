// Regla clinica de NutriSalud: anemia en ninos de 6 a 59 meses (g/dL).
// Fuente: NTS N° 213-MINSA/DGIESP-2024, que adopta la guia OMS 2024.
// Si la norma cambia, SOLO se modifica este archivo.
export type NivelAnemia =
'normal' | 'leve' | 'moderada' | 'severa' | 'sin-criterio';
// Limite inferior (inclusive) de cada nivel, por grupo de edad.
interface Cortes { normal: number; leve: number; moderada: number; }
const CORTES_6_A_23_MESES: Cortes = { normal: 10.5, leve: 9.5, moderada: 7.0 };
const CORTES_24_A_59_MESES: Cortes = { normal: 11.0, leve: 10.0, moderada: 7.0 };
// Por debajo de 7.0 g/dL la anemia es severa en ambos grupos.
/** g/dL que se restan a la Hb observada por la altitud de residencia. */
export function ajustePorAltitud(msnm: number): number {
if (msnm <= 0) return 0;
const gPorLitro = 0.0056384 * msnm + 0.0000003 * msnm * msnm; // OMS 2024
return gPorLitro / 10; // g/L -> g/dL
}
/** Hb corregida, con un decimal como el resultado del laboratorio. */
export function hbAjustada(hb: number, msnm: number): number {
return Math.round((hb - ajustePorAltitud(msnm)) * 10) / 10;
}

export function clasificarAnemia(hb: number, edadMeses: number,
msnm: number): NivelAnemia {
// 1. La norma de ninos solo cubre de 6 a 59 meses
if (edadMeses < 6 || edadMeses > 59) return 'sin-criterio';
// 2. La edad decide que tabla de cortes se usa
const c = edadMeses <= 23 ? CORTES_6_A_23_MESES : CORTES_24_A_59_MESES;
// 3. Se compara la Hb YA corregida por altitud, de arriba hacia abajo
const valor = hbAjustada(hb, msnm);
if (valor >= c.normal) return 'normal';
if (valor >= c.leve) return 'leve';
if (valor >= c.moderada) return 'moderada';
return 'severa';
}
/** Para filtros y contadores: leve, moderada y severa cuentan como anemia. */
export function tieneAnemia(nivel: NivelAnemia): boolean {
return nivel === 'leve' || nivel === 'moderada' || nivel === 'severa';
}