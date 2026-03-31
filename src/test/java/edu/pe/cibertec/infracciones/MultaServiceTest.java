package edu.pe.cibertec.infracciones;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import edu.pe.cibertec.infracciones.exception.InfractorBloqueadoException;
import edu.pe.cibertec.infracciones.model.EstadoMulta;
import edu.pe.cibertec.infracciones.model.Infractor;
import edu.pe.cibertec.infracciones.model.Multa;
import edu.pe.cibertec.infracciones.model.Vehiculo;
import edu.pe.cibertec.infracciones.repository.InfractorRepository;
import edu.pe.cibertec.infracciones.repository.MultaRepository;
import edu.pe.cibertec.infracciones.service.impl.MultaServiceImpl;

@ExtendWith(MockitoExtension.class)
public class MultaServiceTest {

    @Mock
    private MultaRepository multaRepository;

    @Mock
    private InfractorRepository infractorRepository;

    @InjectMocks
    private MultaServiceImpl multaService;

    private Multa multaPendiente;
    private Infractor infractorA;
    private Infractor infractorB;
    private Vehiculo vehiculoComun;

    @BeforeEach
    void setUp() {
      
        vehiculoComun = new Vehiculo();
        vehiculoComun.setId(10L);
        vehiculoComun.setPlaca("ABC-123");

    
        infractorA = new Infractor();
        infractorA.setId(1L);
        infractorA.setNombre("Infractor A");

  
        infractorB = new Infractor();
        infractorB.setId(2L);
        infractorB.setNombre("Infractor B");
        infractorB.setBloqueado(false);
        infractorB.setVehiculos(new ArrayList<>(List.of(vehiculoComun)));

        multaPendiente = new Multa();
        multaPendiente.setId(1L);
        multaPendiente.setEstado(EstadoMulta.PENDIENTE);
        multaPendiente.setInfractor(infractorA);
        multaPendiente.setVehiculo(vehiculoComun);
    }

    // --- PREGUNTA 3 Y PARTE DE LA 4 (ESCENARIO ÉXITO + ARGUMENT CAPTOR) ---
    @Test
    void transferirMulta_DebeAsignarMultaAInfractorB_CuandoEsValido() {
      
        Long multaId = 1L;
        Long nuevoInfractorId = 2L;
        
        ArgumentCaptor<Multa> multaCaptor = ArgumentCaptor.forClass(Multa.class);

        when(multaRepository.findById(multaId)).thenReturn(Optional.of(multaPendiente));
        when(infractorRepository.findById(nuevoInfractorId)).thenReturn(Optional.of(infractorB));
        when(multaRepository.save(any(Multa.class))).thenReturn(multaPendiente);

        multaService.transferirMulta(multaId, nuevoInfractorId);

        // Verificamos que se llamó exactamente una vez y capturamos (Pregunta 4)
        verify(multaRepository, times(1)).save(multaCaptor.capture());
        
        Multa multaGuardada = multaCaptor.getValue();
        assertEquals(nuevoInfractorId, multaGuardada.getInfractor().getId(), 
            "La multa capturada debería estar asignada al ID del Infractor B");
    }

    // --- PREGUNTA 4 (ESCENARIO ERROR + VERIFY NEVER) ---
    @Test
    void transferirMulta_DebeLanzarInfractorBloqueadoException_CuandoInfractorBEstaBloqueado() {
      
        Long multaId = 1L;
        Long nuevoInfractorId = 2L;
        
      
        infractorB.setBloqueado(true);

        when(multaRepository.findById(multaId)).thenReturn(Optional.of(multaPendiente));
        when(infractorRepository.findById(nuevoInfractorId)).thenReturn(Optional.of(infractorB));

    
        assertThrows(InfractorBloqueadoException.class, () -> {
            multaService.transferirMulta(multaId, nuevoInfractorId);
        });

     
        verify(multaRepository, never()).save(any(Multa.class));
    }
}