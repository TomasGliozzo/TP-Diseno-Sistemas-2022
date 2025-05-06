package domain.modelo.entities.HuellaCarbono;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter @Setter
public class HistorialHC {
    private List<String> anios;
    private List<Double> valores;

    public HistorialHC() {
        anios = new ArrayList<>();
        valores = new ArrayList<>();
    }

    public void agregar(HC hc) {
        anios.add(hc.getPeriodo());
        valores.add(hc.getValor());
    }
}
