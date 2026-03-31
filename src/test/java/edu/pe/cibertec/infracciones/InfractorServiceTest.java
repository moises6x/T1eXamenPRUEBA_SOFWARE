package edu.pe.cibertec.infracciones;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import edu.pe.cibertec.infracciones.model.EstadoMulta;
import edu.pe.cibertec.infracciones.model.Multa;
import edu.pe.cibertec.infracciones.repository.MultaRepository;
import edu.pe.cibertec.infracciones.service.impl.InfractorServiceImpl;

@ExtendWith(MockitoExtension.class)
public class InfractorServiceTest {

    @Mock
    private MultaRepository multaRepository;

    @InjectMocks
    private InfractorServiceImpl infractorService;

    @Test
    @DisplayName("Pregunta 1: Debería calcular la deuda con recargo del 15% para multas vencidas")
    void calcularDeudaTest() {
        Long infractorId = 1L;

        Multa multaPendiente = new Multa();
        multaPendiente.setMonto(200.00);
        multaPendiente.setEstado(EstadoMulta.PENDIENTE);

        Multa multaVencida = new Multa();
        multaVencida.setMonto(300.00);
        multaVencida.setEstado(EstadoMulta.VENCIDA);

        List<Multa> multasSimuladas = Arrays.asList(multaPendiente, multaVencida);

        when(multaRepository.findByInfractor_Id(infractorId)).thenReturn(multasSimuladas);

        Double deudaTotal = infractorService.calcularDeuda(infractorId);

        assertEquals(545.00, deudaTotal, "La deuda calculada no coincide con el escenario de prueba");
    }
}