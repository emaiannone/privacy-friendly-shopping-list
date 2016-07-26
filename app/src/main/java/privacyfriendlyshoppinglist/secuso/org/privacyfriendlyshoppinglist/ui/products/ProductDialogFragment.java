package privacyfriendlyshoppinglist.secuso.org.privacyfriendlyshoppinglist.ui.products;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;
import privacyfriendlyshoppinglist.secuso.org.privacyfriendlyshoppinglist.R;
import privacyfriendlyshoppinglist.secuso.org.privacyfriendlyshoppinglist.framework.context.AbstractInstanceFactory;
import privacyfriendlyshoppinglist.secuso.org.privacyfriendlyshoppinglist.framework.context.InstanceFactory;
import privacyfriendlyshoppinglist.secuso.org.privacyfriendlyshoppinglist.framework.utils.StringUtils;
import privacyfriendlyshoppinglist.secuso.org.privacyfriendlyshoppinglist.logic.product.business.ProductService;
import privacyfriendlyshoppinglist.secuso.org.privacyfriendlyshoppinglist.logic.product.business.domain.ProductDto;

/**
 * Created by Chris on 18.07.2016.
 */
public class ProductDialogFragment extends DialogFragment
{
    private ProductActivityCache cache;
    private ProductDto dto;
    private ProductService productService;

    private LinearLayout expandableLayout;
    private TextView expandableHint;
    private Button expandableButton;

    private EditText productName;
    private EditText quantity;
    private EditText price;
    private EditText customStore;
    private EditText category;
    private EditText productNotes;

    private Button buttonPlus;
    private Button buttonMinus;
    private CheckBox productCheckBox;

    public static ProductDialogFragment newInstance(ProductDto dto, ProductActivityCache cache)
    {
        ProductDialogFragment dialogFragment = new ProductDialogFragment();
        dialogFragment.setCache(cache);
        dialogFragment.setDto(dto);
        AbstractInstanceFactory instanceFactory = new InstanceFactory(cache.getActivity().getApplicationContext());
        ProductService productService = (ProductService) instanceFactory.createInstance(ProductService.class);
        dialogFragment.setProductService(productService);
        return dialogFragment;
    }

    public void setCache(ProductActivityCache cache)
    {
        this.cache = cache;
    }

    public void setDto(ProductDto dto)
    {
        this.dto = dto;
    }

    public void setProductService(ProductService productService)
    {
        this.productService = productService;
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        // Read DTO contents and activity from cache
        Activity activity = cache.getActivity();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(getActivity().LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.product_dialog, null);

        productName = (EditText) v.findViewById(R.id.product_name);
        quantity = (EditText) v.findViewById(R.id.quantity);
        price = (EditText) v.findViewById(R.id.product_price);
        customStore = (EditText) v.findViewById(R.id.store_input);
        category = (EditText) v.findViewById(R.id.category_input);
        productNotes = (EditText) v.findViewById(R.id.product_notes);

        buttonPlus = (Button) v.findViewById(R.id.product_button_plus);
        buttonMinus = (Button) v.findViewById(R.id.product_button_minus);
        productCheckBox = (CheckBox) v.findViewById(R.id.product_checkbox);

        expandableLayout = (LinearLayout) v.findViewById(R.id.expandable_product_view);
        expandableHint = (TextView) v.findViewById(R.id.expand_text);
        expandableButton = (Button) v.findViewById(R.id.expand_button);


        expandableLayout.setVisibility(View.GONE);

        productName.setText(dto.getProductName());
        productNotes.setText(dto.getProductNotes());
        quantity.setText(dto.getQuantity());
        price.setText(dto.getProductPrice());
        category.setText(dto.getProductCategory());
        customStore.setText(dto.getProductStore());

        if ( dto.isChecked() )
        {
            productCheckBox.setChecked(true);
        }

        productCheckBox.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if ( !productCheckBox.isChecked() )
                {
                    productCheckBox.setChecked(true);
                }
                else
                {
                    productCheckBox.setChecked(false);
                }

            }
        });

        buttonPlus.setOnClickListener(new View.OnClickListener()
        {
            int value;
            String newQuantity;

            @Override
            public void onClick(View view)
            {
                if ( !StringUtils.isEmpty(String.valueOf(quantity.getText())) )
                {
                    value = Integer.parseInt(String.valueOf(quantity.getText()));
                    value++;
                    newQuantity = String.valueOf(value);
                    quantity.setText(newQuantity);
                }
            }
        });

        buttonMinus.setOnClickListener(new View.OnClickListener()
        {
            int value;
            String newQuantity;

            @Override
            public void onClick(View view)
            {
                value = Integer.parseInt(String.valueOf(quantity.getText()));
                if ( value > 0 )
                {
                    value--;
                    newQuantity = String.valueOf(value);
                    quantity.setText(newQuantity);
                }
            }
        });


        expandableButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if ( expandableLayout.getVisibility() == View.GONE )
                {
                    expandableLayout.setVisibility(View.VISIBLE);
                    expandableHint.setText("Collapse Details");
                }
                else
                {
                    expandableLayout.setVisibility(View.GONE);
                    expandableHint.setText("Expand Details");
                }
            }
        });


        builder.setPositiveButton(cache.getActivity().getResources().getString(R.string.okay), new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                dto.setProductName(String.valueOf(productName.getText()));
                dto.setProductNotes(String.valueOf(productNotes.getText()));
                dto.setQuantity(String.valueOf(quantity.getText()));
                dto.setProductPrice(String.valueOf(price.getText()));
                dto.setProductCategory(String.valueOf(category.getText()));
                dto.setProductStore(String.valueOf(customStore.getText()));

                if ( productCheckBox.isChecked() )
                {
                    dto.setChecked(true);
                }

                productService.saveOrUpdate(dto, cache.getListId());
                ProductsActivity productsActivity = (ProductsActivity) cache.getActivity();
                productsActivity.updateListView();
            }
        });

        builder.setNegativeButton(cache.getActivity().getResources().getString(R.string.cancel), new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
            }
        });

        builder.setView(v);
        return builder.create();
    }

}
