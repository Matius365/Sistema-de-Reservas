# Análisis del Dominio – API Sistema de Reservas

## 1. Descripción General

El sistema permite a los usuarios realizar reservas sobre recursos disponibles (salas o instalaciones) en una fecha y franja horaria determinada.

La API REST diseñada permite gestionar completamente el ciclo de vida de una reserva mediante operaciones CRUD.

La entidad principal es **Reserva**, que se relaciona con las tablas **Usuario** y **Recurso**.

---

## 2. Decisiones de Diseño

### Identificador único en API

Aunque la base de datos puede utilizar clave compuesta, en la API se expone un identificador único `id` para simplificar el consumo REST y seguir buenas prácticas de diseño.

### Separación de modelos

Se definen dos modelos distintos:

- `Reserva`: modelo completo devuelto por el servidor.
- `ReservaInput`: modelo utilizado para crear o modificar reservas, sin campos generados automáticamente.

Esta separación evita que el cliente envíe datos que deben ser gestionados por el backend.

---

## 3. Campos expuestos

### Modelo Reserva (respuesta del servidor)

- id
- usuario_id
- recurso_id
- fecha
- hora_inicio
- hora_fin
- numero_plazas
- coste
- estado
- motivo
- observaciones
- created_at
- updated_at

### Modelo ReservaInput (entrada)

- recurso_id
- fecha
- hora_inicio
- hora_fin
- numero_plazas
- motivo
- observaciones

No se exponen contraseñas ni datos sensibles del usuario.

---

## 4. Reglas de Negocio

1. La fecha debe ser futura.
2. hora_inicio debe ser anterior a hora_fin.
3. numero_plazas debe ser mayor que 0.
4. numero_plazas no puede superar la capacidad del recurso.
5. No pueden existir reservas solapadas para el mismo recurso.
6. El estado de la reserva puede ser:
   - pendiente
   - confirmada
   - cancelada
7. Solo pueden eliminarse reservas en estado pendiente o confirmada.

---

## 5. Validación y Documentación

La especificación OpenAPI ha sido validada en Swagger Editor sin errores.
La documentación interactiva se ha generado con starting-swagger.

![alt text](<Captura de pantalla 2026-02-28 a las 16.03.47.png>) ![alt text](<Captura de pantalla 2026-02-28 a las 16.04.10.png>) ![alt text](<Captura de pantalla 2026-02-28 a las 16.04.21.png>)![alt text](<Captura de pantalla 2026-02-28 a las 14.02.20.png>) ![alt text](<Captura de pantalla 2026-02-28 a las 14.02.34.png>) ![alt text](<Captura de pantalla 2026-02-28 a las 14.02.44.png>)