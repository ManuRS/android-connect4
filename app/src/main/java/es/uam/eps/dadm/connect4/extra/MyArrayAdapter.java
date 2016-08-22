package es.uam.eps.dadm.connect4.extra;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import es.uam.eps.dadm.connect4.R;

/**
 * Created by
 * @author Gonzalo
 * on 22/02/16.
 */
public class MyArrayAdapter extends ArrayAdapter<String> {

    private class Holder {
        TextView textView;
        ImageView imageView;
    }

    public MyArrayAdapter(Context context, String[] cadenas) {
        super(context, R.layout.my_list_item, cadenas);

    }

    /**
     *  Este método es el que se llama cada vez que ListView tiene que mostrar un nuevo elemento de
     *  la lista. Lo que debe hacer este método es devolver la vista que se va a mostrar para el
     *  elemento.
     *
     *  En este ejemplo hemos creado tres versiones de getView que hemos puesto en otras funciones
     *  por claridad. En realidad solo es necesario implementar una de las tres opciones y poner el
     *  código directamente en getView
     *
     *  Ver más detalles en getView0, getView1 y getView2
     */

    /**
     * Versión más eficiente. Evita también el uso de findViewById guardando una referencia directa
     * a los elementos del layout fila que necesitamos modificar. Este método puede ser entorno a un
     * 15% más rápido que el anterior.
     *
     */

    /**
     * Por simplicidad dejo solo la version mas eficiente
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View vistaLinea;
        Holder holder;

        vistaLinea = convertView;
        if ( vistaLinea == null ) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            vistaLinea = inflater.inflate(R.layout.my_list_item, parent, false);
            // Guardamos en esta clase la referencia directa al TextView e ImageView
            holder = new Holder();
            holder.imageView = (ImageView) vistaLinea.findViewById(R.id.imagen1);
            holder.textView  = (TextView) vistaLinea.findViewById(R.id.textNick);
            // El holder se guarda en el Tag de la Vista, que sirve para almacenar un Object
            vistaLinea.setTag(holder);
        }

        // Recuperamos el holder de las filas ya creadas
        holder = (Holder)vistaLinea.getTag();
        holder.textView.setText(getItem(position));

        if ( position%2 == 0 ) {
            holder.imageView.setImageResource(R.mipmap.ic_launcher);
            vistaLinea.setBackgroundColor(getContext().getResources().getColor(R.color.colorList));
        }
        else {
            holder.imageView.setImageResource(R.mipmap.ic_launcher);
            vistaLinea.setBackgroundColor(Color.WHITE);
        }

        return vistaLinea;
    }
}
