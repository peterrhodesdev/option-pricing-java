# Option Pricing

Detailed analytic and numerical calculations of financial option values and their Greeks.

Analytically calculated option values/greeks:
- European call and put

Option pricing models:
- Cox, Ross, and Rubinstein (1979)

[Examples](#examples) \
[Get the code](#get-the-code) \
[Run the tests](#run-the-tests) \
[Build the jar](#build-the-jar)

## Examples

References:
- Hull (2014): Hull, J. (2014) Options, Futures and Other Derivatives. 9th Edition, Prentice Hall, Upper Saddle River.
- Hull SSM (2014): Hull, J. (2014) Student Solutions Manual for Options, Futures, and Other Derivatives. 9th Edition, Prentice Hall, Upper Saddle River.

### Analytically priced options

_Hull SSM (2014), page 166, Problem 15.13_: European call option

```java
// S_0 = 52, K = 50, τ = 0.25, σ = 0.3, r = 0.12, q = 0
AnalyticOption option = AnalyticOptionFactory.createEuropeanCall(52, 50, 0.25, 0.3, 0.12, 0);
double price = option.price();
assertThat(price).isEqualTo(5.06, withPrecision(0.01));
```

Calling `option.calculation()` will return an `AnalyticCalculation` model with details about the pricing calculation. The `getSteps()` method returns a `String[][]` which contains the LaTeX mathematical expressions for the calculation steps. The equations are split by line and equals sign, leaving the formatting up to the user. For example, the parts can be joined and wrapped in the LaTeX `align` environment with:

```java
String[][] steps = option.calculation().getSteps();
String latex = "\\begin{align*} ";
for (String[] step : steps) {
    latex += "& ";
    latex += String.join(" = ", step);
    latex += " \\\\";
}
latex += " \\end{align*}";
```

Which will produce the following LaTeX expression:

![EuropeanLatex](https://user-images.githubusercontent.com/40833704/135256871-ffa6737c-ca7e-4518-ae48-0c0f8cf76172.gif)

Similar functionality is provided for the following Greeks: delta, gamma, vega, theta, rho.

### Cox, Ross, and Rubinstein

_Hull SSM (2014): page 142, Problem 13.17_: American put option, 2 time steps

```java
// S_0 = 1500, K = 1480, τ = 1, σ = 0.18, r = 0.04, q = 0.025
Option option = new OptionBuilder(1500, 1480, 1, 0.18, 0.04, 0.025)
    .styleAmerican()
    .typePut()
    .build();
int timeSteps = 2;
PricingModel pricingModel = PricingModelSelector.coxRossRubinstein(timeSteps);
double price = pricingModel.price(option);
assertThat(price).isEqualTo(78.41, withPrecision(0.01));
```

Calling `pricingModel.calculation()` will return a model with details about the pricing calculation, i.e. the tree parameters along with an array of the tree nodes. The return value of the method must be typed to the appropriate calculation model (`CoxRossRubinstein`). e.g.

```java
PricingModel<CoxRossRubinstein> pricingModel = PricingModelSelector.coxRossRubinstein(timeSteps);
CoxRossRubinstein result = pricingModel.calculation(option);
System.out.println(
    String.format("Δt = %f, u = %f, d = %f, p = %f",
        result.getDeltat(), result.getU(), result.getD(), result.getP())
);
for (CoxRossRubinstein.Node node : result.getNodes()) {
    System.out.println(
        String.format("i = %d, j = %d, S = %f, V = %f, exercised = %b",
            node.getI(), node.getJ(), node.getS(), node.getV(), node.getExercised())
    );
}
```

Will produce the following output:

```
Δt = 0.500000, u = 1.135734, d = 0.880488, p = 0.497717
i = 0, j = 0, S = 1500.000000, V = 78.413718, exercised = false
i = 1, j = 0, S = 1320.731682, V = 159.268318, exercised = true
i = 1, j = 1, S = 1703.601141, V = 0.000000, exercised = false
i = 2, j = 0, S = 1162.888117, V = 317.111883, exercised = true
i = 2, j = 1, S = 1500.000000, V = 0.000000, exercised = false
i = 2, j = 2, S = 1934.837898, V = 0.000000, exercised = false
```

## Get the code

Use one of the methods given below to get the project source code on your local machine.

### Clone

SSH:

```bash
git clone git@github.com:peterrhodesdev/option-pricing-java.git
```

HTTPS:

```bash
git clone https://github.com/peterrhodesdev/option-pricing-java.git
```

GitHub CLI:

```bash
gh repo clone peterrhodesdev/option-pricing-java
```

### Fork and clone

```bash
gh repo fork peterrhodesdev/option-pricing-java --clone=true
```

### Add a remote + pull

```bash
mkdir option-pricing-java
cd option-pricing-java
git init
git remote add option-pricing-java git@github.com:peterrhodesdev/option-pricing-java.git
git pull option-pricing-java main
```

## Run the tests

Run the below command from the project root directory to run the test suite.

```bash
mvn verify
```

This will also apply the following code stylers/formatters:
- checkstyle
- spotbugs
- PMD

## Build the jar

Run the below command from the project root directory to create the option-pricing-${project.version}.jar file.

```bash
mvn package
```
