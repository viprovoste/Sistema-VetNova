package vetnova.inventario.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import vetnova.inventario.dto.ProveedorRequest;
import vetnova.inventario.dto.ProveedorResponse;
import vetnova.inventario.exception.ResourceNotFoundException;
import vetnova.inventario.model.Proveedor;
import vetnova.inventario.repository.ProveedorRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProveedorService - pruebas unitarias")
class ProveedorServiceTest {

    @Mock
    private ProveedorRepository proveedorRepository;

    @InjectMocks
    private ProveedorServiceImpl proveedorService;

    private Proveedor proveedorVetMed;
    private ProveedorRequest requestVetMed;

    @BeforeEach
    void setUp() {
        proveedorVetMed = Proveedor.builder()
                .id(1L)
                .nombre("VetMed Suministros")
                .rut("76123456-7")
                .contacto("Marcela Ríos")
                .telefono("+56222334455")
                .email("contacto@vetmed.cl")
                .direccion("Av. Industrial 450, Chillán")
                .activo(true)
                .build();

        requestVetMed = new ProveedorRequest("VetMed Suministros", "76123456-7", "Marcela Ríos",
                "+56222334455", "contacto@vetmed.cl", "Av. Industrial 450, Chillán");
    }

    @Nested
    @DisplayName("Creación y consulta")
    class CreacionConsulta {

        @Test
        @DisplayName("Crea un proveedor correctamente")
        void creaProveedor() {
            when(proveedorRepository.save(any(Proveedor.class))).thenReturn(proveedorVetMed);

            ProveedorResponse response = proveedorService.crear(requestVetMed);

            assertThat(response.getNombre()).isEqualTo("VetMed Suministros");
            assertThat(response.getActivo()).isTrue();
        }

        @Test
        @DisplayName("Lista todos los proveedores")
        void listaTodos() {
            when(proveedorRepository.findAll()).thenReturn(List.of(proveedorVetMed));

            List<ProveedorResponse> resultado = proveedorService.listarTodos();

            assertThat(resultado).hasSize(1);
        }

        @Test
        @DisplayName("Obtiene un proveedor por ID")
        void obtienePorId() {
            when(proveedorRepository.findById(1L)).thenReturn(Optional.of(proveedorVetMed));

            ProveedorResponse response = proveedorService.obtenerPorId(1L);

            assertThat(response.getRut()).isEqualTo("76123456-7");
        }

        @Test
        @DisplayName("Lanza excepción si el proveedor no existe")
        void lanzaExcepcionSiNoExiste() {
            when(proveedorRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> proveedorService.obtenerPorId(99L))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("Actualización y baja")
    class ActualizacionBaja {

        @Test
        @DisplayName("Actualiza los datos de un proveedor")
        void actualizaProveedor() {
            ProveedorRequest cambios = new ProveedorRequest("VetMed Suministros SpA", "76123456-7",
                    "Marcela Ríos", "+56222334400", "ventas@vetmed.cl", "Nueva dirección 100");

            when(proveedorRepository.findById(1L)).thenReturn(Optional.of(proveedorVetMed));
            when(proveedorRepository.save(any(Proveedor.class))).thenAnswer(inv -> inv.getArgument(0));

            ProveedorResponse response = proveedorService.actualizar(1L, cambios);

            assertThat(response.getNombre()).isEqualTo("VetMed Suministros SpA");
            assertThat(response.getEmail()).isEqualTo("ventas@vetmed.cl");
        }

        @Test
        @DisplayName("Desactiva un proveedor")
        void desactivaProveedor() {
            when(proveedorRepository.findById(1L)).thenReturn(Optional.of(proveedorVetMed));
            when(proveedorRepository.save(any(Proveedor.class))).thenAnswer(inv -> inv.getArgument(0));

            ProveedorResponse response = proveedorService.desactivar(1L);

            assertThat(response.getActivo()).isFalse();
        }
    }
}
