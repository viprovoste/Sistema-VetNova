# Sistema-VetNova

Proyecto académico para el ramo de Fullstack, desarrollado con una arquitectura de **microservicios en Java**.

## Descripción

Vetnova es una plataforma para la gestión integral de una clínica veterinaria. El sistema está dividido en microservicios independientes, cada uno con su propia base de datos, encargados de un dominio específico del negocio.

## Arquitectura de Microservicios

| Microservicio | Responsabilidad | Base de Datos |
|---|---|---|
| **Autenticación (Auth)** | Login, registro de credenciales, permisos y roles (Admin, Vet, Recepcionista, Cliente). | `vetnova_auth` |
| **Clientes y Mascotas** | Gestión de perfiles de dueños y de pacientes (mascotas). | `vetnova_clientes` |
| **Agendamiento y Sucursales** | Gestión de horas médicas, disponibilidad de boxes y configuración de sedes. | `vetnova_agendamiento` |
| **Clínico** | Fichas clínicas, diagnósticos, recetas y órdenes de exámenes. | `vetnova_clinico` |
| **Inventario y Bodega** | Control de stock (insumos y e-commerce), entradas/salidas y pedidos a proveedores. | `vetnova_inventario` |
| **Ventas y Facturación** | Carrito de compras web, pagos, caja en sucursal y emisión de boletas/facturas. | `vetnova_ventas` |
| **Soporte y Fidelización (Post-venta)** | Consultas web, reclamos y valoraciones de atención. | `vetnova_soporte` |
| **Notificaciones** | Servicio transversal para enviar recordatorios (desacoplado del resto). | `vetnova_notificaciones` |
| **Reportes (Analytics)** | Consume datos del resto para generar estadísticas para los administradores. | `vetnova_reportes` |

## Tecnologías

- **Lenguaje:** Java
- **Arquitectura:** Microservicios (un servicio + una base de datos por dominio)
- **Tipo:** Proyecto Fullstack

## Equipo

* Vicente Provoste
* Marcel Chamblas
* Sebatian Miranda
