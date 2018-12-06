package com.comerciomovil.ecommerce.borrega.ui.interfaces;



import com.comerciomovil.ecommerce.borrega.requests.CartRequest;
import com.comerciomovil.ecommerce.borrega.requests.ChangeStatusOrderRequest;
import com.comerciomovil.ecommerce.borrega.requests.CreateOrderRequest;
import com.comerciomovil.ecommerce.borrega.requests.RegisterDeviceRequest;
import com.comerciomovil.ecommerce.borrega.requests.UpdateShoppingCartRequest;
import com.comerciomovil.ecommerce.borrega.requests.UserOperationRequest;
import com.comerciomovil.ecommerce.borrega.responses.ConfigurationResponse;
import com.comerciomovil.ecommerce.borrega.responses.CreateOrderResponse;
import com.comerciomovil.ecommerce.borrega.responses.GenericResponse;
import com.comerciomovil.ecommerce.borrega.responses.GetCartResponse;
import com.comerciomovil.ecommerce.borrega.responses.GetContactsResponse;
import com.comerciomovil.ecommerce.borrega.responses.GetOrdersResponse;
import com.comerciomovil.ecommerce.borrega.responses.GetPaymentMethodResponse;
import com.comerciomovil.ecommerce.borrega.responses.GetProductsResponse;
import com.comerciomovil.ecommerce.borrega.responses.GetShippingAddressResponse;
import com.comerciomovil.ecommerce.borrega.responses.OrderDetailResponse;
import com.comerciomovil.ecommerce.borrega.responses.UserResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by administrator on 19/07/2017.
 */
public interface WebServicesInterface {
    @GET("getProducts/{start}/{end}")
    Call<GetProductsResponse> getAllProducts(
            @Path("start") String start, @Path("end") String end);

    @GET("getLocationsByUser/{id_user}")
    Call<GetShippingAddressResponse> getLocationsByUser(
            @Path("id_user") String id_user);

    @GET("getContactsByUser/{id_user}")
    Call<GetContactsResponse> getContactsByUser(
            @Path("id_user") String id_user);

    @GET("getPaymentMethods")
    Call<GetPaymentMethodResponse> getPaymentMethods();

    @Headers({"Content-Type: application/json", "Cache-Control: max-age=640000"})
    @POST("cartOperation")
    Call<GenericResponse> shoppingCart(@Body CartRequest cart);

    @GET("getShoppingCart/{id_user}")
    Call<GetCartResponse> getShoppingCart(
            @Path("id_user") String id_user);

    @Headers({"Content-Type: application/json", "Cache-Control: max-age=640000"})
    @POST("createOrder")
    Call<CreateOrderResponse> createOrder(@Body CreateOrderRequest order);

    @Headers({"Content-Type: application/json", "Cache-Control: max-age=640000"})
    @POST("updateShoppingCart")
    Call<GenericResponse> updateShoppingCart(@Body UpdateShoppingCartRequest request);

    @GET("getOrders/{id_user}/{id_profile}/{start}/{end}/{days}")
    Call<GetOrdersResponse> getOrders(
            @Path("id_user") String id_user, @Path("id_profile") String id_profile, @Path("start") int start, @Path("end") int end, @Path("days") String days);

    @GET("getDeliverManOrders/{id_user}/{id_profile}/{type_query}/{start}/{end}/{days}")
    Call<GetOrdersResponse> getDeliverManOrders(
            @Path("id_user") String id_user, @Path("id_profile") String id_profile, @Path("type_query") String type_query, @Path("start") int start, @Path("end") int end, @Path("days") String days);

    @Headers({"Content-Type: application/json", "Cache-Control: max-age=640000"})
    @POST("userOperation")
    Call<UserResponse> userOperation(@Body UserOperationRequest request);

    @GET("getConfiguration")
    Call<ConfigurationResponse> getConfiguration();

    @GET("recoveryPassword/{email}/{newPassword}/{encrypted}")
    Call<GenericResponse> recoveryPassword(
            @Path("email") String email, @Path("newPassword") String newPassword, @Path("encrypted") String encrypted);

    @Headers({"Content-Type: application/json", "Cache-Control: max-age=640000"})
    @POST("changeStatusOrder")
    Call<GenericResponse> ChangeStatusOrder(@Body ChangeStatusOrderRequest request);

    @Headers({"Content-Type: application/json", "Cache-Control: max-age=640000"})
    @POST("registerDevice")
    Call<GenericResponse> registerDevice(@Body RegisterDeviceRequest request);

    @GET("getDetailOrder/{id_order}")
    Call<OrderDetailResponse> getDetailOrder(
            @Path("id_order") String id_order);
}
