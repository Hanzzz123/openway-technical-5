# End to End Flow

1. access `https://www.periplus.com/`  
2. Sign in with email and password from variable in .env file (email has to be already registered first). You can look at the .env.example file for reference.
3. Search for the book **"The Daily Dad: 366 Meditations on Parenting"**  
4. Enter first image that shows up.  
5. Add to cart.  
6. Go to cart and verify, if name of the product == the product that we added to cart, test Passed

# How to Run the Program
**Perequisites**: make sure maven, jdk, google chrome is already installed.
1. Run `docker-compose up --build`
2. After building run `docker-compose run selenium`
3. go to `/src/test/java.com.example/PeriPlusCartTest.java`, right click it, then press run, or go to file and run it. You can either run it locally or with docker container.
4. Watch the automation.
`

# Video 
[Testing Locally](https://drive.google.com/file/d/1nd96_ITlZGKcDu64hq_Ar9SqDLlJ9pZ1/view?usp=sharing)  

[Testing Portably with docker](https://drive.google.com/file/d/1HPyzF0mqj_hDy_kkWxcKuDvzEIfRXlCl/view?usp=sharing)
