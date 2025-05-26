# End to End Flow

1. access `https://www.periplus.com/`  
2. Sign in with email and password from variable in .env file (email has to be already registered first). You can look at the .env.example file for reference.
3. Search for the book **"The Daily Dad: 366 Meditations on Parenting"**  
4. Enter first image that shows up.  
5. Add to cart.  
6. Go to cart and verify, if name of the product == the product that we added to cart, test Passed

# How to Run the Program
**Perequisites**: 
1. make sure maven, jdk, google chrome is already installed.
2. Create .env file.
3. Copy .env.exampe to .env, provide your already registered email and password  

## Instruction for running without docker 
1. In .env file, initialize SELENIUM_MODE to LOCAL
 2. go to `/src/test/java.com.example/PeriPlusCartTest.java`
 3. right click it, then press run, or go to file and run it.


## Instruction For running with docker##  
1. In .env file, initialize SELENIUM_MODE to DOCKER
2. Run `docker-compose up --build`
3. After building run `docker-compose run selenium`
4. go to `/src/test/java.com.example/PeriPlusCartTest.java`, right click it, then press run, or go to file and run it.
5. Watch the automation.
Notice: If running with docker, google chrome testing will not show, but end log will show
`

# Video 
[Testing Locally](https://drive.google.com/file/d/1nd96_ITlZGKcDu64hq_Ar9SqDLlJ9pZ1/view?usp=sharing)  

[Testing Portably with docker](https://drive.google.com/file/d/1lJNxaEONZ_Aw2sdTTQoMee6lyrg_FMn6/view?usp=sharing)
