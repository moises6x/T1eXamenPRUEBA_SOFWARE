package edu.pe.cibertec.infracciones;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import edu.pe.cibertec.infracciones.model.EstadoMulta;
import edu.pe.cibertec.infracciones.model.Infractor;
import edu.pe.cibertec.infracciones.model.Vehiculo;
import edu.pe.cibertec.infracciones.repository.InfractorRepository;
import edu.pe.cibertec.infracciones.repository.MultaRepository;
import edu.pe.cibertec.infracciones.repository.VehiculoRepository;
import edu.pe.cibertec.infracciones.service.impl.InfractorServiceImpl;

@ExtendWith(MockitoExtension.class)
public class desasignarVehiculoExitoTest {

    @Mock
    private MultaRepository multaRepository;

    @Mock
    private InfractorRepository infractorRepository;

    @Mock
    private VehiculoRepository vehiculoRepository;

    @InjectMocks
    private InfractorServiceImpl infractorService;

    @Test
    @DisplayName("Pregunta 2: Debe remover el vehículo si no hay multas PENDIENTES")
    void desasignarVehiculoEscenarioExito() {
       
        Long idInf = 1L;
        Long idVeh = 1L;

   
        Vehiculo vehiculo = new Vehiculo();
        vehiculo.setId(idVeh);

        Infractor infractor = new Infractor();
        infractor.setId(idInf);
        infractor.setVehiculos(new ArrayList<>());
        infractor.getVehiculos().add(vehiculo);

        when(multaRepository.findByInfractor_IdAndVehiculo_IdAndEstado(idInf, idVeh, EstadoMulta.PENDIENTE))
            .thenReturn(Collections.emptyList());

        when(infractorRepository.findById(idInf)).thenReturn(Optional.of(infractor));

        infractorService.desasignarVehiculo(idInf, idVeh);

        assertTrue(infractor.getVehiculos().isEmpty(), "El vehículo debería haber sido removido");
        
        verify(infractorRepository, times(1)).save(infractor);
    }
}