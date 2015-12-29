
trait Character {
  val name: String
  var health: Int
  def armor: Int
  def actions: List[Action]

  def beforeTurn(opponent: Character) { }
  def afterTurn(opponent: Character) { }

  override def toString = name
  def isAlive = health > 0

  def describe = s"${getClass.getSimpleName} $name (health: $health, armor: $armor)"

  def vs(character: Character) = Turn(this, character)

  override def clone: Character = super.clone.asInstanceOf[Character]
}

trait Action {
  def performOn(actor: Character, opponent: Character)
}

object Nothing extends Action {
  override def performOn(actor: Character, opponent: Character): Unit = ()
  override val toString = "does nothing to"
}

object Suicide extends Action {
  override def performOn(actor: Character, opponent: Character): Unit = {
    actor.health = 0
  }
  override val toString = "kills himself, wins"
}

// --- Wizard -----------------------------------------------------------------

class Wizard( val name:   String,
              var health: Int,
              var mana:   Int,
              val spells: List[Spell]) extends Character with Cloneable {

  def castable(spell: Spell) =
    spell.cost <= mana && (spell.effects forall {effects.getOrElse(_, 0) == 0})

  def actions = spells filter castable match {
    case Nil => List( Suicide )
    case some => some
  }

  private var effects: Map[Effect,Int] = Map()
  private var addedEffects: List[Effect] = Nil

  def startEffect(e: Effect): Unit = {
    addedEffects ::= e
  }

  override def beforeTurn(opponent: Character): Unit = {
    effects.keys foreach {_.apply(this,opponent)}
    effects = effects mapValues (_ - 1)
  }
  override def afterTurn(opponent: Character): Unit = {
    effects.keys foreach {_.unapply(this,opponent)}
    effects = effects filter {case (_, t: Int) => t > 0}
    effects ++= addedEffects map {e => e -> e.turns}
    addedEffects = Nil
  }

  override def describe =
    s"${getClass.getSimpleName} $name (health: $health, armor: $armor, mana: $mana)" +
      (if (effects.isEmpty) "" else "\n" + effects)

  var armor = 0
}

trait Spell extends Action {
  val cost: Int
  val effects: List[Effect] = Nil
  def castOn(wizard: Wizard, opponent: Character)
  override def performOn(actor: Character, opponent: Character) {
    val owner = actor.asInstanceOf[Wizard]
    owner.mana -= cost
    castOn(owner, opponent)
    effects foreach owner.startEffect
  }
  override def toString = Spell.this.toString
}

case class Cast(cost: Int, effect: Effect) extends Spell {
  override val effects = List(effect)

  override def castOn(wizard: Wizard, opponent: Character) { }

  override val toString = s"casts $effect on"
}

trait Effect {
  val turns: Int
  def apply(wizard: Wizard, opponent: Character) { }
  def unapply(wizard: Wizard, opponent: Character) { }

  def costs(mana: Int) = Cast(mana, this)
}

case class MagicMissile(cost:   Int,
                        damage: Int) extends Spell {

  override def castOn(wizard: Wizard, opponent: Character) =
      opponent.health -= damage

  override val toString = s"casts Magic Missile with $damage damage on"
}

case class Drain(cost:   Int,
                 amount: Int) extends Spell {

  override def castOn(wizard: Wizard, opponent: Character) {
    opponent.health -= amount
    wizard.health += amount
  }

  override val toString = s"casts Drain and takes $amount health from"
}

case class Shield(turns: Int,
                  armor: Int) extends Effect {

  override def apply(wizard: Wizard, opponent: Character): Unit = {
    wizard.armor += armor
  }
  override def unapply(wizard: Wizard, opponent: Character): Unit = {
    wizard.armor -= armor
  }

  override val toString = s"$armor points shield"
}

case class Poison(turns:  Int,
                  damage: Int) extends Effect {

  override def apply(wizard: Wizard, opponent: Character): Unit = {
    opponent.health -= damage
  }
  override val toString = s"$damage points poison"
}

case class Recharge(turns: Int,
                    mana:  Int) extends Effect {

  override def apply(wizard: Wizard, opponent: Character): Unit = {
    wizard.mana += mana
  }
  override val toString = s"$mana points recharge"
}

// --- Warrior ----------------------------------------------------------------

class Warrior( val name:   String,
               var health: Int,
               val damage: Int,
               val armor:  Int) extends Character with Cloneable {

  val actions = List( PhysicalAttack(damage) )
}

case class PhysicalAttack(damage: Int) extends Action {
  override def performOn(actor: Character, opponent: Character) =
    opponent.health -= Math.max(1, damage - opponent.armor)

  override val toString = s"attacks with $damage damage"
}

// --- Game -------------------------------------------------------------------

case class Move(actor:    Character,
                action:   Action,
                opponent: Character) {

  override val toString = s"$actor $action $opponent"

  lazy val turn = {
    action.performOn(actor, opponent)
    actor.afterTurn(opponent)
    opponent.afterTurn(actor)
    actor.beforeTurn(opponent)
    opponent.beforeTurn(actor)
    Turn(opponent, actor)
  }

  val manaSpent = action match {
    case s: Spell => s.cost
    case _ => 0
  }
}

case class Turn(actor: Character,
                opponent: Character) {

  def nextMoves = actor.actions map {Move(actor.clone, _, opponent.clone)}

  override val toString = s"$actor vs $opponent"

  def isEnded = ! actor.isAlive || ! opponent.isAlive

  def winner =
    if (! isEnded) None
    else if (actor.isAlive) Some(actor)
    else Some(opponent)
}

sealed trait Fight {

  val turn: Turn

  def print(): Unit = {
    this match {
      case Step(m, prev) =>
        prev.print()
        printf("Turn %d:%n", size)
        println(m)
      case _ =>
    }
    println(turn.actor.describe)
    println(turn.opponent.describe)
    turn.winner foreach {winner =>
        printf("%nThe winner is %s%n", winner)
    }
    println("")
  }

  val size: Int = this match {
    case Start(_) => 0
    case Step(_, prev) => 1 + prev.size
  }

  val manaSpent: Int = this match {
    case Start(_) => 0
    case Step(move, prev) => move.manaSpent + prev.manaSpent
  }

  def next(move: Move): Fight = Step(move, this)

  def forks = turn.nextMoves map next

  def traverse[T](init: T)(collect: (T,Fight,=> T) => T): T =
    forks.foldLeft(init)((acc, fight) =>
      collect(acc, fight, {fight.traverse(acc)(collect)}))
}

case class Start(override val turn: Turn) extends Fight
case class Step(move: Move, previous: Fight) extends Fight {
  val turn = move.turn
}

// --- Main -------------------------------------------------------------------

object Main extends App {
  val player = new Wizard(
    name = "Merlin",
    health = 50,
    mana = 500,
    spells = List(
      MagicMissile(cost = 53, damage = 4),
      Drain(cost = 73, amount = 2),
      Shield(turns = 6, armor = 7) costs 113,
      Poison(turns = 6, damage = 3) costs 173,
      Recharge(turns = 5, mana = 101) costs 229
    )
  )
  val boss = new Warrior(
    name = "Konan",
    health = 58,
    damage = 9,
    armor = 5
  )

  val solution = Start(player vs boss).traverse(Option.empty[Fight])((maybeBest, current, proceed) =>
    (current.turn.winner, maybeBest) match {
      case (_, Some(best)) if current.manaSpent >= best.manaSpent => maybeBest
      case (Some(winner), _) if winner.isInstanceOf[Wizard] => {
        printf("New best solution found: %d steps and %d mana%n", current.size, current.manaSpent)
        Some(current)
      }
      case (None, _) => proceed
      case _ => maybeBest
    })

  solution match {
    case None => println("Wizard can't win")
    case Some(fight) =>
      fight.print()
      printf("Total mana spent: %d%n", fight.manaSpent)
  }
}